package com.example.ui.components

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.IptvChannel

@Composable
fun MultiScreenPlayerLayout(
    mode: MultiScreenMode,
    screenChannels: List<IptvChannel?>,
    activeAudioIndex: Int,
    volumeLevel: Float,
    onSelectAudioIndex: (Int) -> Unit,
    onRequestChannelPicker: (Int) -> Unit,
    onPreviousChannel: (Int) -> Unit = {},
    onNextChannel: (Int) -> Unit = {},
    onToggleControls: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("multi_screen_player_layout")
    ) {
        when (mode) {
            MultiScreenMode.SINGLE -> {
                // Single screen handled by parent or fallback
            }
            MultiScreenMode.DUAL -> {
                // Mode 2 Layar: Atas (Layar 1) dan Bawah (Layar 2)
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        SingleMultiScreenPane(
                            screenIndex = 0,
                            screenTitle = "Layar 1 (Atas)",
                            channel = screenChannels.getOrNull(0),
                            isAudioActive = (activeAudioIndex == 0),
                            volumeLevel = volumeLevel,
                            onSelectAudio = { onSelectAudioIndex(0) },
                            onRequestChannelPicker = { onRequestChannelPicker(0) },
                            onPreviousChannel = { onPreviousChannel(0) },
                            onNextChannel = { onNextChannel(0) },
                            onToggleControls = onToggleControls,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Pembatas tipis emas mewah antar layar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0x33FFD700),
                                        Color(0xFFFFD700),
                                        Color(0x33FFD700)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        SingleMultiScreenPane(
                            screenIndex = 1,
                            screenTitle = "Layar 2 (Bawah)",
                            channel = screenChannels.getOrNull(1),
                            isAudioActive = (activeAudioIndex == 1),
                            volumeLevel = volumeLevel,
                            onSelectAudio = { onSelectAudioIndex(1) },
                            onRequestChannelPicker = { onRequestChannelPicker(1) },
                            onPreviousChannel = { onPreviousChannel(1) },
                            onNextChannel = { onNextChannel(1) },
                            onToggleControls = onToggleControls,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            MultiScreenMode.QUAD -> {
                // Mode 4 Layar:
                // Bagian atas: Layar 1 (Kiri) dan Layar 2 (Kanan)
                // Bagian bawah: Layar 3 (Kiri) dan Layar 4 (Kanan)
                Column(modifier = Modifier.fillMaxSize()) {
                    // Baris Atas
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Layar 1: Kiri Atas
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            SingleMultiScreenPane(
                                screenIndex = 0,
                                screenTitle = "Layar 1 (Kiri Atas)",
                                channel = screenChannels.getOrNull(0),
                                isAudioActive = (activeAudioIndex == 0),
                                volumeLevel = volumeLevel,
                                onSelectAudio = { onSelectAudioIndex(0) },
                                onRequestChannelPicker = { onRequestChannelPicker(0) },
                                onPreviousChannel = { onPreviousChannel(0) },
                                onNextChannel = { onNextChannel(0) },
                                onToggleControls = onToggleControls,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Divider vertikal
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0x33FFD700), Color(0xFFFFD700), Color(0x33FFD700))
                                    )
                                )
                        )

                        // Layar 2: Kanan Atas
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            SingleMultiScreenPane(
                                screenIndex = 1,
                                screenTitle = "Layar 2 (Kanan Atas)",
                                channel = screenChannels.getOrNull(1),
                                isAudioActive = (activeAudioIndex == 1),
                                volumeLevel = volumeLevel,
                                onSelectAudio = { onSelectAudioIndex(1) },
                                onRequestChannelPicker = { onRequestChannelPicker(1) },
                                onPreviousChannel = { onPreviousChannel(1) },
                                onNextChannel = { onNextChannel(1) },
                                onToggleControls = onToggleControls,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Divider horizontal tengah
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0x33FFD700), Color(0xFFFFD700), Color(0x33FFD700))
                                )
                            )
                    )

                    // Baris Bawah
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Layar 3: Kiri Bawah
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            SingleMultiScreenPane(
                                screenIndex = 2,
                                screenTitle = "Layar 3 (Kiri Bawah)",
                                channel = screenChannels.getOrNull(2),
                                isAudioActive = (activeAudioIndex == 2),
                                volumeLevel = volumeLevel,
                                onSelectAudio = { onSelectAudioIndex(2) },
                                onRequestChannelPicker = { onRequestChannelPicker(2) },
                                onPreviousChannel = { onPreviousChannel(2) },
                                onNextChannel = { onNextChannel(2) },
                                onToggleControls = onToggleControls,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Divider vertikal
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0x33FFD700), Color(0xFFFFD700), Color(0x33FFD700))
                                    )
                                )
                        )

                        // Layar 4: Kanan Bawah
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            SingleMultiScreenPane(
                                screenIndex = 3,
                                screenTitle = "Layar 4 (Kanan Bawah)",
                                channel = screenChannels.getOrNull(3),
                                isAudioActive = (activeAudioIndex == 3),
                                volumeLevel = volumeLevel,
                                onSelectAudio = { onSelectAudioIndex(3) },
                                onRequestChannelPicker = { onRequestChannelPicker(3) },
                                onPreviousChannel = { onPreviousChannel(3) },
                                onNextChannel = { onNextChannel(3) },
                                onToggleControls = onToggleControls,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun SingleMultiScreenPane(
    screenIndex: Int,
    screenTitle: String,
    channel: IptvChannel?,
    isAudioActive: Boolean,
    volumeLevel: Float,
    onSelectAudio: () -> Unit,
    onRequestChannelPicker: () -> Unit,
    onPreviousChannel: () -> Unit = {},
    onNextChannel: () -> Unit = {},
    onToggleControls: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isBuffering by remember { mutableStateOf(false) }
    var streamError by remember { mutableStateOf<String?>(null) }

    val player = remember(context) {
        PlayerStreamHelper.createExoPlayer(context)
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        streamError = null
                    }
                    Player.STATE_ENDED, Player.STATE_IDLE -> {
                        isBuffering = false
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    try {
                        player.seekToDefaultPosition()
                        player.prepare()
                        player.play()
                        return
                    } catch (_: Exception) {}
                }
                isBuffering = false
                streamError = "Siaran offline"
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    // Sync volume: only active screen outputs sound
    LaunchedEffect(isAudioActive, volumeLevel) {
        player.volume = if (isAudioActive) volumeLevel else 0f
    }

    // Load stream when channel changes
    LaunchedEffect(channel) {
        streamError = null
        PlayerStreamHelper.loadStream(channel, player)
    }

    val goldAccent = Color(0xFFFFD700)
    val paneNumber = screenIndex + 1

    Box(
        modifier = modifier
            .background(Color.Black)
            .border(
                if (isAudioActive) 1.5.dp else 0.5.dp,
                if (isAudioActive) goldAccent else Color(0xFF2A2B3D)
            )
            .testTag("multi_screen_pane_$paneNumber")
    ) {
        // Video View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setKeepContentOnPlayerReset(true)
                }
            },
            update = { view ->
                if (view.player !== player) {
                    view.player = player
                }
            },
            onRelease = { view ->
                view.player = null
            },
            modifier = Modifier.fillMaxSize()
        )

        // Touch surface: Single tap switches audio & toggles controls. Double tap center opens popup; double tap top/bottom switches channel.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(onSelectAudio, onRequestChannelPicker, onPreviousChannel, onNextChannel) {
                    detectTapGestures(
                        onTap = {
                            onSelectAudio()
                            onToggleControls()
                        },
                        onDoubleTap = { offset ->
                            val isCenterX = offset.x >= (size.width * 0.22f) && offset.x <= (size.width * 0.78f)
                            val isCenterY = offset.y >= (size.height * 0.22f) && offset.y <= (size.height * 0.78f)
                            if (isCenterX && isCenterY) {
                                // Ketuk 2x di Tengah Layar: Otomatis munculkan kotak pop up pemilihan channel
                                onRequestChannelPicker()
                            } else {
                                val isTopHalf = offset.y < (size.height * 0.5f)
                                if (isTopHalf) {
                                    // Ketuk 2x Kanan/Kiri Atas: Ganti channel sebelumnya
                                    onPreviousChannel()
                                } else {
                                    // Ketuk 2x Kanan/Kiri Bawah: Ganti channel selanjutnya
                                    onNextChannel()
                                }
                            }
                        }
                    )
                }
        )

        // Overlay Header (Top Bar of Each Pane)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                    )
                )
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Screen Number Badge & Channel Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAudioActive) goldAccent else Color(0xFF2A2B3D),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = "Layar $paneNumber",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAudioActive) Color.Black else Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = channel?.name ?: "Belum Memilih Channel",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAudioActive) goldAccent else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action Buttons: Audio Status & Ganti Channel
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Audio Toggle / Indicator
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAudioActive) goldAccent.copy(alpha = 0.25f) else Color(0xFF1E1F2C).copy(alpha = 0.8f),
                    border = BorderStroke(
                        0.8.dp,
                        if (isAudioActive) goldAccent else Color(0xFF4A4D68)
                    ),
                    modifier = Modifier
                        .clickable { onSelectAudio() }
                        .testTag("audio_toggle_pane_$paneNumber")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAudioActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isAudioActive) "Suara Aktif" else "Bisu",
                            tint = if (isAudioActive) goldAccent else Color.LightGray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isAudioActive) "Suara" else "Bisu",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAudioActive) goldAccent else Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))

                // Tombol "Ganti Channel" (Membuka popup pilihan channel untuk layar ini)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = goldAccent,
                    modifier = Modifier
                        .clickable { onRequestChannelPicker() }
                        .testTag("pick_channel_pane_$paneNumber")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = "Pilih Channel",
                            tint = Color.Black,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Ganti",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Center Placeholder if no channel selected (Wajib memilih siaran)
        if (channel == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.78f))
                    .clickable { onRequestChannelPicker() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Layar $paneNumber: Belum Ada Siaran",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Wajib memilih siaran untuk menonton di layar ini",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onRequestChannelPicker,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = goldAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("empty_state_pick_channel_button_$paneNumber")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pilih Channel Layar $paneNumber",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Buffering Indicator
        AnimatedVisibility(
            visible = isBuffering,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = goldAccent,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Error message banner
        if (streamError != null && channel != null) {
            Surface(
                color = Color.Red.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .clickable {
                        PlayerStreamHelper.loadStream(channel, player)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Coba Lagi",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streamError (Ketuk untuk coba lagi)",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
