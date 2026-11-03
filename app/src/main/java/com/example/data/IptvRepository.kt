package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class IptvRepository private constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val _channelsFlow = MutableStateFlow<List<IptvChannel>>(emptyList())
    val channelsFlow: StateFlow<List<IptvChannel>> = _channelsFlow.asStateFlow()

    private val _isUpdatingFlow = MutableStateFlow(false)
    val isUpdatingFlow: StateFlow<Boolean> = _isUpdatingFlow.asStateFlow()

    companion object {
        const val PREFS_NAME = "iptv_preferences"
        const val KEY_PLAYLIST_SOURCES_V3 = "playlist_sources_v3"
        private const val CACHE_FILE_NAME = "iptv_channels_cache.json"

        @Volatile
        private var INSTANCE: IptvRepository? = null

        fun getInstance(context: Context? = null): IptvRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = IptvRepository()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_PLAYLIST_SOURCES = listOf(
            PlaylistSource(
                id = "default_adult_1",
                name = "Adult IPTV Source 1",
                urlOrContent = "https://raw.githubusercontent.com/atsushi444/iptv/refs/heads/main/adult.m3u",
                type = PlaylistSourceType.URL,
                isEnabled = true
            ),
            PlaylistSource(
                id = "default_adult_2",
                name = "Adult IPTV Source 2",
                urlOrContent = "https://raw.githubusercontent.com/atsushi444/iptv/refs/heads/main/adult2.m3u",
                type = PlaylistSourceType.URL,
                isEnabled = true
            )
        )
    }

    /**
     * Mengambil channel secara instan dari memori flow -> local cache file -> preloaded defaults.
     * Tidak memblokir UI dan langsung mengembalikan data (0ms).
     */
    fun getCachedChannels(context: Context): List<IptvChannel> {
        val inMemory = _channelsFlow.value
        if (inMemory.isNotEmpty()) {
            return inMemory
        }

        val fromFile = loadChannelsFromFile(context)
        if (fromFile.isNotEmpty()) {
            _channelsFlow.value = fromFile
            return fromFile
        }

        val defaults = getPreloadedDefaultChannels()
        _channelsFlow.value = defaults
        return defaults
    }

    /**
     * Secara otomatis mengupdate semua sumber playlist IPTV di background secara paralel
     * saat aplikasi baru dibuat atau dipanggil.
     */
    suspend fun autoUpdateAllSources(context: Context): List<IptvChannel> = withContext(Dispatchers.IO) {
        if (_isUpdatingFlow.value) {
            return@withContext getCachedChannels(context)
        }
        _isUpdatingFlow.value = true
        try {
            val freshChannels = fetchChannels(context)
            if (freshChannels.isNotEmpty()) {
                _channelsFlow.value = freshChannels
                saveChannelsToFile(context, freshChannels)
                return@withContext freshChannels
            }
        } catch (e: Exception) {
            Log.e("IptvRepository", "Error during autoUpdateAllSources: ${e.message}")
        } finally {
            _isUpdatingFlow.value = false
        }
        return@withContext getCachedChannels(context)
    }

    /**
     * Membaca channel dari file cache JSON lokal
     */
    private fun loadChannelsFromFile(context: Context): List<IptvChannel> {
        try {
            val file = File(context.filesDir, CACHE_FILE_NAME)
            if (!file.exists()) return emptyList()
            val jsonStr = file.readText()
            if (jsonStr.isBlank()) return emptyList()

            val array = JSONArray(jsonStr)
            val list = ArrayList<IptvChannel>(array.length())
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    IptvChannel(
                        id = obj.optString("id", "ch_$i"),
                        name = obj.optString("name", "Channel $i"),
                        genre = obj.optString("genre", "Semua"),
                        logoUrl = obj.optString("logoUrl", ""),
                        streamUrl = obj.optString("streamUrl", ""),
                        licenseType = obj.optString("licenseType", ""),
                        licenseKey = obj.optString("licenseKey", ""),
                        userAgent = obj.optString("userAgent", ""),
                        referer = obj.optString("referer", ""),
                        isMpd = obj.optBoolean("isMpd", false)
                    )
                )
            }
            return list
        } catch (e: Exception) {
            Log.e("IptvRepository", "Error loading cached channels from file: ${e.message}")
            return emptyList()
        }
    }

    /**
     * Menyimpan channel ke file cache JSON lokal
     */
    private fun saveChannelsToFile(context: Context, channels: List<IptvChannel>) {
        try {
            val array = JSONArray()
            for (ch in channels) {
                val obj = JSONObject()
                obj.put("id", ch.id)
                obj.put("name", ch.name)
                obj.put("genre", ch.genre)
                obj.put("logoUrl", ch.logoUrl)
                obj.put("streamUrl", ch.streamUrl)
                obj.put("licenseType", ch.licenseType)
                obj.put("licenseKey", ch.licenseKey)
                obj.put("userAgent", ch.userAgent)
                obj.put("referer", ch.referer)
                obj.put("isMpd", ch.isMpd)
                array.put(obj)
            }
            val file = File(context.filesDir, CACHE_FILE_NAME)
            file.writeText(array.toString())
        } catch (e: Exception) {
            Log.e("IptvRepository", "Error saving channels to cache file: ${e.message}")
        }
    }

    /**
     * Default starter channels agar layar Live TV langsung memiliki siaran aktif
     */
    fun getPreloadedDefaultChannels(): List<IptvChannel> {
        return listOf(
            IptvChannel(
                id = "preloaded_ch_1",
                name = "驚豔台 (Amazing TV)",
                genre = "PRIVADO",
                logoUrl = "https://raw.githubusercontent.com/atsushi444/iptv/main/logo/amazing_tv.png",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/85"
            ),
            IptvChannel(
                id = "preloaded_ch_2",
                name = "潘朵啦玩美台 (Pandora Perfect)",
                genre = "PRIVADO",
                logoUrl = "https://raw.githubusercontent.com/atsushi444/iptv/main/logo/pandora_perfect.png",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/86"
            ),
            IptvChannel(
                id = "preloaded_ch_3",
                name = "惊艳台",
                genre = "PRIVADO",
                logoUrl = "",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/87"
            ),
            IptvChannel(
                id = "preloaded_ch_4",
                name = "松視1台 (SonSee 1)",
                genre = "PRIVADO",
                logoUrl = "https://raw.githubusercontent.com/atsushi444/iptv/main/logo/sonsee1.png",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/88"
            ),
            IptvChannel(
                id = "preloaded_ch_5",
                name = "松視2台 (SonSee 2)",
                genre = "PRIVADO",
                logoUrl = "https://raw.githubusercontent.com/atsushi444/iptv/main/logo/SonSee2.png",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/89"
            ),
            IptvChannel(
                id = "preloaded_ch_6",
                name = "松視3台 (SonSee 3)",
                genre = "PRIVADO",
                logoUrl = "https://raw.githubusercontent.com/atsushi444/iptv/main/logo/sonsee3.png",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/90"
            ),
            IptvChannel(
                id = "preloaded_ch_7",
                name = "松視4台 (SonSee 4)",
                genre = "PRIVADO",
                logoUrl = "https://raw.githubusercontent.com/atsushi444/iptv/main/logo/sonsee4.png",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/91"
            ),
            IptvChannel(
                id = "preloaded_ch_8",
                name = "彩虹電影台",
                genre = "PRIVADO",
                logoUrl = "https://raw.githubusercontent.com/atsushi444/iptv/main/logo/Rainbow%20Movie.png",
                streamUrl = "http://15.204.105.50:25461/G2s9zK2n9m/xDtwVfWM8T/92"
            )
        )
    }

    fun getSavedPlaylistSources(context: Context): List<PlaylistSource> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_PLAYLIST_SOURCES_V3, null)
        if (!jsonString.isNullOrBlank()) {
            val list = deserializeSources(jsonString)
            if (list.isNotEmpty()) return list
        }

        // Migrate from legacy URL list if present
        val legacyUrls = getLegacyPlaylistUrls(context)
        val migratedList = mutableListOf<PlaylistSource>()
        for ((idx, url) in legacyUrls.withIndex()) {
            migratedList.add(
                PlaylistSource(
                    id = "url_${idx}_${url.hashCode()}",
                    name = "URL Source ${idx + 1}",
                    urlOrContent = url,
                    type = PlaylistSourceType.URL,
                    isEnabled = true
                )
            )
        }

        val customText = prefs.getString("custom_m3u_text", null)
        if (!customText.isNullOrBlank()) {
            migratedList.add(
                PlaylistSource(
                    id = "custom_text_legacy",
                    name = "Teks M3U Manual",
                    urlOrContent = customText,
                    type = PlaylistSourceType.CUSTOM_TEXT,
                    isEnabled = true
                )
            )
        }

        val initialList = if (migratedList.isNotEmpty()) migratedList else DEFAULT_PLAYLIST_SOURCES
        savePlaylistSources(context, initialList)
        return initialList
    }

    fun savePlaylistSources(context: Context, sources: List<PlaylistSource>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = serializeSources(sources)
        prefs.edit().putString(KEY_PLAYLIST_SOURCES_V3, jsonString).apply()
    }

    fun togglePlaylistSourceEnabled(context: Context, sourceId: String, isEnabled: Boolean) {
        val current = getSavedPlaylistSources(context).toMutableList()
        val index = current.indexOfFirst { it.id == sourceId }
        if (index != -1) {
            current[index] = current[index].copy(isEnabled = isEnabled)
            savePlaylistSources(context, current)
        }
    }

    fun addPlaylistSource(context: Context, source: PlaylistSource) {
        val current = getSavedPlaylistSources(context).toMutableList()
        current.removeAll { it.urlOrContent.trim() == source.urlOrContent.trim() && it.type == source.type }
        current.add(0, source)
        savePlaylistSources(context, current)
    }

    fun removePlaylistSource(context: Context, sourceId: String) {
        val current = getSavedPlaylistSources(context).toMutableList()
        current.removeAll { it.id == sourceId }
        savePlaylistSources(context, current)
    }

    fun resetPlaylists(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_PLAYLIST_SOURCES_V3)
            .remove("playlist_urls_v2")
            .remove("playlist_urls")
            .remove("custom_m3u_text")
            .apply()
        savePlaylistSources(context, DEFAULT_PLAYLIST_SOURCES)
    }

    private fun getLegacyPlaylistUrls(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedString = prefs.getString("playlist_urls_v2", null) ?: return emptyList()
        return savedString.split("\n").map { it.trim() }.filter { it.isNotBlank() }
    }

    private fun serializeSources(sources: List<PlaylistSource>): String {
        val array = JSONArray()
        for (src in sources) {
            val obj = JSONObject()
            obj.put("id", src.id)
            obj.put("name", src.name)
            obj.put("urlOrContent", src.urlOrContent)
            obj.put("type", src.type.name)
            obj.put("isEnabled", src.isEnabled)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeSources(jsonString: String): List<PlaylistSource> {
        val list = mutableListOf<PlaylistSource>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "src_$i")
                val name = obj.optString("name", "Playlist Source")
                val urlOrContent = obj.optString("urlOrContent", "")
                val typeStr = obj.optString("type", PlaylistSourceType.URL.name)
                val type = try { PlaylistSourceType.valueOf(typeStr) } catch (_: Exception) { PlaylistSourceType.URL }
                val isEnabled = obj.optBoolean("isEnabled", true)

                list.add(PlaylistSource(id, name, urlOrContent, type, isEnabled))
            }
        } catch (e: Exception) {
            Log.e("IptvRepository", "Error deserializing playlist sources: ${e.message}")
        }
        return list
    }

    /**
     * Mengambil dan mem-parsing seluruh playlist M3U aktif secara PARALEL untuk kecepatan maksimal.
     */
    suspend fun fetchChannels(context: Context? = null): List<IptvChannel> = withContext(Dispatchers.IO) {
        if (context == null) return@withContext emptyList()

        val sources = getSavedPlaylistSources(context)
        val enabledSources = sources.filter { it.isEnabled }

        val results = coroutineScope {
            enabledSources.map { source ->
                async(Dispatchers.IO) {
                    fetchSourceChannels(source)
                }
            }.awaitAll()
        }

        val allChannels = mutableListOf<IptvChannel>()
        val seenStreamUrls = HashSet<String>()

        for (sourceList in results) {
            for (ch in sourceList) {
                // Deduplikasi channel berdasarkan streamUrl
                if (ch.streamUrl.isNotBlank() && seenStreamUrls.add(ch.streamUrl)) {
                    allChannels.add(ch)
                }
            }
        }

        if (allChannels.isNotEmpty()) {
            _channelsFlow.value = allChannels
            saveChannelsToFile(context, allChannels)
            Log.d("IptvRepository", "Total unique channels fetched: ${allChannels.size}")
            return@withContext allChannels
        }

        // Jika network gagal / kosong, kembalikan data cache
        return@withContext getCachedChannels(context)
    }

    private fun fetchSourceChannels(source: PlaylistSource): List<IptvChannel> {
        when (source.type) {
            PlaylistSourceType.URL -> {
                val url = source.urlOrContent.trim()
                if (url.isNotBlank()) {
                    try {
                        val request = Request.Builder()
                            .url(url)
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                            .build()

                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                val bodyString = response.body?.string() ?: ""
                                val parsed = parseM3u(bodyString)
                                if (parsed.isNotEmpty()) {
                                    Log.d("IptvRepository", "Parsed ${parsed.size} channels from URL source ${source.name}")
                                    return parsed
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("IptvRepository", "Error fetching M3U playlist from ${source.name}: ${e.message}")
                    }
                }
            }
            PlaylistSourceType.LOCAL_FILE, PlaylistSourceType.CUSTOM_TEXT -> {
                if (source.urlOrContent.isNotBlank()) {
                    val parsed = parseM3u(source.urlOrContent)
                    if (parsed.isNotEmpty()) {
                        Log.d("IptvRepository", "Parsed ${parsed.size} channels from local/custom source ${source.name}")
                        return parsed
                    }
                }
            }
        }
        return emptyList()
    }

    private fun parseM3u(content: String): List<IptvChannel> {
        val channels = mutableListOf<IptvChannel>()
        val lines = content.lines()

        var currentLogo = ""
        var currentGenre = "Umum"
        var currentName = ""
        var currentLicenseType = ""
        var currentLicenseKey = ""
        var currentUserAgent = ""
        var currentReferer = ""
        var currentIsMpd = false
        var channelCount = 0

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:", ignoreCase = true)) {
                // Parse tvg-logo
                val logoMatch = Regex("""tvg-logo="([^"]+)"""", RegexOption.IGNORE_CASE).find(trimmed)
                currentLogo = logoMatch?.groupValues?.get(1)?.trim() ?: ""

                // Parse group-title (Genre)
                val groupMatch = Regex("""group-title="([^"]+)"""", RegexOption.IGNORE_CASE).find(trimmed)
                currentGenre = groupMatch?.groupValues?.get(1)?.trim()?.ifBlank { "Umum" } ?: "Umum"

                // Parse Channel Name (after last comma)
                val commaIndex = trimmed.lastIndexOf(',')
                currentName = if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                    trimmed.substring(commaIndex + 1).trim()
                } else {
                    ""
                }
            } else if (trimmed.startsWith("#KODIPROP:", ignoreCase = true)) {
                val prop = trimmed.substringAfter(":").trim()
                val eqIdx = prop.indexOf('=')
                if (eqIdx != -1) {
                    val key = prop.substring(0, eqIdx).trim().lowercase()
                    val value = prop.substring(eqIdx + 1).trim()
                    when {
                        key.contains("license_type") -> currentLicenseType = value
                        key.contains("license_key") -> currentLicenseKey = value
                        key.contains("manifest_type") && value.equals("mpd", ignoreCase = true) -> currentIsMpd = true
                    }
                }
            } else if (trimmed.startsWith("#EXTVLCOPT:", ignoreCase = true)) {
                val opt = trimmed.substringAfter(":").trim()
                val eqIdx = opt.indexOf('=')
                if (eqIdx != -1) {
                    val key = opt.substring(0, eqIdx).trim().lowercase()
                    val value = opt.substring(eqIdx + 1).trim()
                    when {
                        key.contains("http-user-agent") -> currentUserAgent = value
                        key.contains("http-referrer") || key.contains("http-referer") -> currentReferer = value
                    }
                }
            } else if (trimmed.startsWith("#EXTHTTP:", ignoreCase = true)) {
                try {
                    val jsonStr = trimmed.substringAfter(":", "").trim()
                    if (jsonStr.startsWith("{")) {
                        val json = JSONObject(jsonStr)
                        if (json.has("User-Agent")) currentUserAgent = json.getString("User-Agent")
                        else if (json.has("user-agent")) currentUserAgent = json.getString("user-agent")
                        if (json.has("Referer")) currentReferer = json.getString("Referer")
                        else if (json.has("referer")) currentReferer = json.getString("referer")
                    }
                } catch (_: Exception) {}
            } else if (trimmed.isNotBlank() && !trimmed.startsWith("#")) {
                // Valid stream URL line
                channelCount++
                val channelName = currentName.ifBlank { "Channel $channelCount" }
                val cleanUrl = trimmed.trim().replace("\r", "")
                val lowerUrl = cleanUrl.lowercase()
                val detectedMpd = currentIsMpd ||
                    lowerUrl.contains(".mpd") ||
                    lowerUrl.contains("format=mpd") ||
                    lowerUrl.contains("type=dash") ||
                    lowerUrl.contains("manifest.mpd") ||
                    lowerUrl.contains("/manifest(format=mpd")

                channels.add(
                    IptvChannel(
                        id = "ch_${cleanUrl.hashCode().toString().replace("-", "n")}_$channelCount",
                        name = channelName,
                        genre = sanitizeGenre(currentGenre),
                        logoUrl = currentLogo,
                        streamUrl = cleanUrl,
                        licenseType = currentLicenseType,
                        licenseKey = currentLicenseKey,
                        userAgent = currentUserAgent,
                        referer = currentReferer,
                        isMpd = detectedMpd
                    )
                )

                // Reset temporary holders
                currentLogo = ""
                currentGenre = "Umum"
                currentName = ""
                currentLicenseType = ""
                currentLicenseKey = ""
                currentUserAgent = ""
                currentReferer = ""
                currentIsMpd = false
            }
        }

        return channels
    }

    private fun sanitizeGenre(rawGenre: String): String {
        val clean = rawGenre.replace("\"", "").replace("'", "").trim()
        return if (clean.isNotBlank()) clean else "Semua"
    }

    fun getFavoriteChannelIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet("favorite_channel_ids_v1", emptySet()) ?: emptySet()
    }

    fun isChannelFavorite(context: Context, channelId: String): Boolean {
        return getFavoriteChannelIds(context).contains(channelId)
    }

    fun toggleFavoriteChannel(context: Context, channelId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getStringSet("favorite_channel_ids_v1", emptySet())?.toMutableSet() ?: mutableSetOf()
        val isFav: Boolean
        if (current.contains(channelId)) {
            current.remove(channelId)
            isFav = false
        } else {
            current.add(channelId)
            isFav = true
        }
        prefs.edit().putStringSet("favorite_channel_ids_v1", current).apply()
        return isFav
    }

    fun getSavedSortMode(context: Context): ChannelSortMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString("channel_sort_mode_v1", ChannelSortMode.ORIGINAL.name)
        return try {
            ChannelSortMode.valueOf(name ?: ChannelSortMode.ORIGINAL.name)
        } catch (_: Exception) {
            ChannelSortMode.ORIGINAL
        }
    }

    fun saveSortMode(context: Context, mode: ChannelSortMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("channel_sort_mode_v1", mode.name).apply()
    }
}
