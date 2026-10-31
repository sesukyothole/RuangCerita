package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.data.IptvChannel

enum class MultiScreenMode(val title: String, val subtitle: String, val count: Int) {
    SINGLE("1 Layar", "Layar Tunggal", 1),
    DUAL("2 Layar", "Atas (Layar 1) & Bawah (Layar 2)", 2),
    QUAD("4 Layar", "Grid 2x2: Layar 1, 2, 3, 4", 4)
}

@OptIn(UnstableApi::class)
object PlayerStreamHelper {

    fun createExoPlayer(context: Context): ExoPlayer {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(20000)
            .setReadTimeoutMs(20000)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,
                /* maxBufferMs = */ 60_000,
                /* bufferForPlaybackMs = */ 2_500,
                /* bufferForPlaybackAfterRebufferMs = */ 5_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setExceedRendererCapabilitiesIfNecessary(true))
        }

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .build().apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace("-", "").trim()
        val len = clean.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len - 1) {
            data[i / 2] = ((Character.digit(clean[i], 16) shl 4) + Character.digit(clean[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun buildClearKeyDataUri(kidHex: String, keyHex: String): Uri? {
        return try {
            val kidBytes = hexToBytes(kidHex)
            val keyBytes = hexToBytes(keyHex)
            val kidB64 = Base64.encodeToString(kidBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val keyB64 = Base64.encodeToString(keyBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val jwk = """{"keys":[{"kty":"oct","k":"$keyB64","kid":"$kidB64"}],"type":"temporary"}"""
            val b64Jwk = Base64.encodeToString(jwk.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            Uri.parse("data:application/json;base64,$b64Jwk")
        } catch (_: Exception) {
            null
        }
    }

    fun loadStream(channel: IptvChannel?, player: ExoPlayer) {
        if (channel != null && channel.streamUrl.isNotBlank()) {
            var rawUrl = channel.streamUrl.trim()
            val customHeaders = mutableMapOf<String, String>()

            if (channel.userAgent.isNotBlank()) {
                customHeaders["User-Agent"] = channel.userAgent
            }
            if (channel.referer.isNotBlank()) {
                customHeaders["Referer"] = channel.referer
            }

            // Parsing header format pipe IPTV (contoh: http://domain.com/live.mpd|User-Agent=...&Referer=...)
            if (rawUrl.contains("|")) {
                val parts = rawUrl.split("|", limit = 2)
                rawUrl = parts[0].trim()
                val headerPairs = parts[1].split("&")
                for (pair in headerPairs) {
                    val kv = pair.split("=", limit = 2)
                    if (kv.size == 2) {
                        val k = kv[0].trim()
                        val v = kv[1].trim()
                        if (k.isNotBlank() && v.isNotBlank()) {
                            customHeaders[k] = v
                        }
                    }
                }
            }

            val lowerUrl = rawUrl.lowercase()
            val isMpd = channel.isMpd ||
                lowerUrl.contains(".mpd") ||
                lowerUrl.contains("format=mpd") ||
                lowerUrl.contains("type=dash") ||
                lowerUrl.contains("manifest.mpd") ||
                lowerUrl.contains("/manifest(format=mpd")

            val defaultUserAgent = customHeaders["User-Agent"]
                ?: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent(defaultUserAgent)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(20000)
                .setReadTimeoutMs(20000)

            val reqProperties = mutableMapOf<String, String>()
            reqProperties["Accept"] = "*/*"
            for ((k, v) in customHeaders) {
                if (!k.equals("User-Agent", ignoreCase = true)) {
                    reqProperties[k] = v
                }
            }
            if (reqProperties.isNotEmpty()) {
                httpDataSourceFactory.setDefaultRequestProperties(reqProperties)
            }

            val mediaItemBuilder = MediaItem.Builder().setUri(Uri.parse(rawUrl))

            if (isMpd) {
                mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)

                val liveConfig = MediaItem.LiveConfiguration.Builder()
                    .setTargetOffsetMs(6000L)
                    .setMinOffsetMs(2500L)
                    .setMaxOffsetMs(25000L)
                    .setMinPlaybackSpeed(0.97f)
                    .setMaxPlaybackSpeed(1.03f)
                    .build()
                mediaItemBuilder.setLiveConfiguration(liveConfig)

                val licKey = channel.licenseKey.trim()
                val licType = channel.licenseType.trim().lowercase()
                if (licKey.isNotBlank()) {
                    val isWidevine = licType.contains("widevine")
                    val drmUuid = if (isWidevine) C.WIDEVINE_UUID else C.CLEARKEY_UUID
                    val drmBuilder = MediaItem.DrmConfiguration.Builder(drmUuid)

                    if (licKey.startsWith("http://", ignoreCase = true) || licKey.startsWith("https://", ignoreCase = true)) {
                        drmBuilder.setLicenseUri(Uri.parse(licKey))
                    } else if (licKey.contains(":") && !licKey.startsWith("{")) {
                        val parts = licKey.split(":")
                        if (parts.size == 2) {
                            val dataUri = buildClearKeyDataUri(parts[0].trim(), parts[1].trim())
                            if (dataUri != null) {
                                drmBuilder.setLicenseUri(dataUri)
                            }
                        }
                    } else if (licKey.startsWith("{")) {
                        val b64 = Base64.encodeToString(licKey.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                        drmBuilder.setLicenseUri(Uri.parse("data:application/json;base64,$b64"))
                    }

                    if (reqProperties.isNotEmpty()) {
                        drmBuilder.setLicenseRequestHeaders(reqProperties)
                    }
                    mediaItemBuilder.setDrmConfiguration(drmBuilder.build())
                }
            } else if (lowerUrl.contains(".m3u8") || lowerUrl.contains("format=m3u8") || lowerUrl.contains("type=hls") || lowerUrl.contains("playlist.m3u8")) {
                mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
            } else if (lowerUrl.contains(".mp4")) {
                mediaItemBuilder.setMimeType(MimeTypes.VIDEO_MP4)
            }

            try {
                val mediaItem = mediaItemBuilder.build()
                player.playWhenReady = true

                if (isMpd) {
                    val chunkSourceFactory = DefaultDashChunkSource.Factory(httpDataSourceFactory)
                    val dashMediaSource = DashMediaSource.Factory(chunkSourceFactory, httpDataSourceFactory)
                        .createMediaSource(mediaItem)
                    player.setMediaSource(dashMediaSource)
                } else {
                    val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)
                    val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)
                    player.setMediaSource(mediaSource)
                }

                player.prepare()
                player.play()
            } catch (_: Exception) {}
        } else {
            try {
                player.stop()
                player.clearMediaItems()
            } catch (_: Exception) {}
        }
    }
}
