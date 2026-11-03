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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.platform.LocalConfiguration
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
    aspectRatioMode: com.example.ui.screens.VideoAspectRatioMode = com.example.ui.screens.VideoAspectRatioMode.FIT,
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
                            aspectRatioMode = aspectRatioMode,
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
                            aspectRatioMode = aspectRatioMode,
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
                                aspectRatioMode = aspectRatioMode,
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
                                aspectRatioMode = aspectRatioMode,
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
                                aspectRatioMode = aspectRatioMode,
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
                                aspectRatioMode = aspectRatioMode,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            MultiScreenMode.HEXA -> {
                // Mode 6 Layar: Grid 2 Kolom dan 3 Baris (Baris 1: Layar 1-2, Baris 2: Layar 3-4, Baris 3: Layar 5-6)
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0..2) {
                        val startIdx = row * 2
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            for (col in 0..1) {
                                val sIndex = startIdx + col
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    SingleMultiScreenPane(
                                        screenIndex = sIndex,
                                        screenTitle = "Layar ${sIndex + 1}",
                                        channel = screenChannels.getOrNull(sIndex),
                                        isAudioActive = (activeAudioIndex == sIndex),
                                        volumeLevel = volumeLevel,
                                        onSelectAudio = { onSelectAudioIndex(sIndex) },
                                        onRequestChannelPicker = { onRequestChannelPicker(sIndex) },
                                        onPreviousChannel = { onPreviousChannel(sIndex) },
                                        onNextChannel = { onNextChannel(sIndex) },
                                        onToggleControls = onToggleControls,
                                        aspectRatioMode = aspectRatioMode,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                if (col == 0) {
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
                                }
                            }
                        }
                        if (row < 2) {
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
                        }
                    }
                }
            }
            MultiScreenMode.OCTA -> {
                // Mode 8 Layar: Grid 4 Baris dan 2 Kolom (Baris 1: Layar 1-2, Baris 2: Layar 3-4, Baris 3: Layar 5-6, Baris 4: Layar 7-8)
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0..3) {
                        val startIdx = row * 2
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            for (col in 0..1) {
                                val sIndex = startIdx + col
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    SingleMultiScreenPane(
                                        screenIndex = sIndex,
                                        screenTitle = "Layar ${sIndex + 1}",
                                        channel = screenChannels.getOrNull(sIndex),
                                        isAudioActive = (activeAudioIndex == sIndex),
                                        volumeLevel = volumeLevel,
                                        onSelectAudio = { onSelectAudioIndex(sIndex) },
                                        onRequestChannelPicker = { onRequestChannelPicker(sIndex) },
                                        onPreviousChannel = { onPreviousChannel(sIndex) },
                                        onNextChannel = { onNextChannel(sIndex) },
                                        onToggleControls = onToggleControls,
                                        aspectRatioMode = aspectRatioMode,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                if (col == 0) {
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
                                }
                            }
                        }
                        if (row < 3) {
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
    aspectRatioMode: com.example.ui.screens.VideoAspectRatioMode = com.example.ui.screens.VideoAspectRatioMode.FIT,
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
                    resizeMode = aspectRatioMode.resizeMode
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setKeepContentOnPlayerReset(true)
                }
            },
            update = { view ->
                if (view.player !== player) {
                    view.player = player
                }
                view.resizeMode = aspectRatioMode.resizeMode
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Audio Toggle / Indicator (26x26dp)
                Surface(
                    onClick = { onSelectAudio() },
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAudioActive) goldAccent.copy(alpha = 0.28f) else Color(0xFF1E1F2C).copy(alpha = 0.8f),
                    border = BorderStroke(
                        0.8.dp,
                        if (isAudioActive) goldAccent else Color(0xFF4A4D68)
                    ),
                    modifier = Modifier
                        .size(26.dp)
                        .testTag("audio_toggle_pane_$paneNumber")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isAudioActive) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = if (isAudioActive) "Suara Aktif" else "Bisu",
                            tint = if (isAudioActive) goldAccent else Color.LightGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Tombol "Ganti Channel" (26x26dp)
                Surface(
                    onClick = { onRequestChannelPicker() },
                    shape = RoundedCornerShape(6.dp),
                    color = goldAccent,
                    modifier = Modifier
                        .size(26.dp)
                        .testTag("pick_channel_pane_$paneNumber")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = "Pilih / Ganti Channel",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
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
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Layar $paneNumber: Belum Ada Siaran",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "Wajib memilih siaran",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onRequestChannelPicker,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = goldAccent,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .heightIn(min = 28.dp)
                            .testTag("empty_state_pick_channel_button_$paneNumber")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pilih Siaran Layar $paneNumber",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
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
