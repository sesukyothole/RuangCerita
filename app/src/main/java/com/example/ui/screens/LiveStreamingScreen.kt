@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.media3.common.util.UnstableApi::class
)

package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.Uri
import android.provider.OpenableColumns
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.util.Base64
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AppThemeMode
import com.example.data.ChannelSortMode
import com.example.data.IptvChannel
import com.example.data.IptvRepository
import com.example.data.PlaylistSource
import com.example.data.PlaylistSourceType
import com.example.ui.components.ChannelPickerPopupDialog
import com.example.ui.components.MultiScreenMode
import com.example.ui.components.MultiScreenModeDialog
import com.example.ui.components.MultiScreenPlayerLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppOrientationMode(val title: String, val subtitle: String) {
    PORTRAIT("Portait", "Mode Layar Tegak"),
    LANDSCAPE("Landscape", "Mode Layar Lebar"),
    AUTO("Automatic", "Sesuai Setingan Device")
}

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamingScreen(
    onCloseToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    currentThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    onThemeModeChange: (AppThemeMode) -> Unit = {},
    onControlsVisibilityChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Disable screen timeout (keep screen awake) during Live TV playback
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    val iptvRepository = remember { IptvRepository() }

    var channels by remember { mutableStateOf<List<IptvChannel>>(emptyList()) }
    var favoriteChannelIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoadingChannels by remember { mutableStateOf(true) }

    val toggleFavoriteChannel: (String) -> Unit = { channelId ->
        iptvRepository.toggleFavoriteChannel(context, channelId)
        favoriteChannelIds = iptvRepository.getFavoriteChannelIds(context)
    }

    // Single Player & Full Screen state
    var currentChannel by remember { mutableStateOf<IptvChannel?>(null) }
    var isBuffering by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var streamError by remember { mutableStateOf<String?>(null) }

    var isFullScreen by remember { mutableStateOf(false) }
    var showControlsInFullScreen by remember { mutableStateOf(true) }

    var lastClickedChannelId by remember { mutableStateOf<String?>(null) }
    var lastClickTimestamp by remember { mutableStateOf(0L) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val channelListHeight = if (isLandscape) 150.dp else 195.dp
    // Jumlah kolom channel dibuat 6 agar visual kartu channel terlihat lebih besar & jelas
    val columnCount = if (isLandscape) 6 else 4

    var showPlaylistSettingsSheet by remember { mutableStateOf(false) }
    var showAudioVolumeDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showOrientationDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showMultiScreenDialog by remember { mutableStateOf(false) }
    var currentMultiScreenMode by remember { mutableStateOf(MultiScreenMode.SINGLE) }
    var multiScreenChannels by remember { mutableStateOf<List<IptvChannel?>>(listOf(null, null, null, null)) }
    var activeAudioIndex by remember { mutableStateOf(0) }
    var pickerTargetScreenIndex by remember { mutableStateOf<Int?>(null) }
    var currentSortMode by remember { mutableStateOf(iptvRepository.getSavedSortMode(context)) }
    // Mode live streaming dipaksa menjadi mode landscape secara default agar menonton lebih leluasa & luas
    var currentOrientationMode by remember { mutableStateOf(AppOrientationMode.LANDSCAPE) }

    val activity = context as? Activity
    fun applyOrientation(mode: AppOrientationMode) {
        currentOrientationMode = mode
        when (mode) {
            AppOrientationMode.AUTO -> {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            AppOrientationMode.PORTRAIT -> {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            AppOrientationMode.LANDSCAPE -> {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
    }

    DisposableEffect(Unit) {
        // Otomatis paksa ke mode landscape saat masuk ke Live Streaming
        applyOrientation(AppOrientationMode.LANDSCAPE)
        onDispose {
            // Pulihkan orientasi layar device saat keluar dari Live Streaming
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Kontrol dan daftar pilihan channel muncul saat awal dibuka,
    // dan akan otomatis sembunyi jika pengguna tidak melakukan aktivitas selama 60 detik.
    // Saat pengguna menyentuh 1x pada layar, pilihan channel dan kontrol otomatis muncul kembali.
    var areControlsVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Timer otomatis sembunyi 60 detik tanpa aktivitas pengguna (diperpanjang agar pengguna leluasa)
    LaunchedEffect(areControlsVisible, lastInteractionTime) {
        if (areControlsVisible) {
            delay(60000L)
            areControlsVisible = false
            onControlsVisibilityChange(false)
        }
    }

    LaunchedEffect(isFullScreen, showControlsInFullScreen, lastInteractionTime) {
        if (isFullScreen && showControlsInFullScreen) {
            delay(60000L)
            showControlsInFullScreen = false
        }
    }

    val toggleControlsVisibility: () -> Unit = {
        if (!areControlsVisible) {
            areControlsVisible = true
            lastInteractionTime = System.currentTimeMillis()
            onControlsVisibilityChange(true)
        } else {
            areControlsVisible = false
            onControlsVisibilityChange(false)
        }
    }

    val recordUserInteraction: () -> Unit = {
        if (!areControlsVisible) {
            areControlsVisible = true
            onControlsVisibilityChange(true)
        }
        lastInteractionTime = System.currentTimeMillis()
    }

    // Theme Mode handling (Light, Dark, System Default)
    val systemIsDark = isSystemInDarkTheme()
    val isDark = when (currentThemeMode) {
        AppThemeMode.SYSTEM -> systemIsDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    // Dynamic Luxury Colors based on Light/Dark Theme
    val themeBgColor = if (isDark) Color(0xFF090A10) else Color(0xFFF1F3F8)
    val themeControlBarBg = if (isDark) Color(0xFF14151E) else Color(0xFFFFFFFF)
    val themeChannelListBg = if (isDark) Color(0xFF0E0F17) else Color(0xFFE8EBF2)
    val themeGoldAccent = if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
    val themeGoldSoft = if (isDark) Color(0x33D4AF37) else Color(0x22B8860B)
    val themeCardBg = if (isDark) Color(0xFF1A1B25) else Color(0xFFFFFFFF)
    val themeCardSelectedBg = if (isDark) Color(0xFF332912) else Color(0xFFFFF6DF)
    val themeTextColor = if (isDark) Color.White else Color(0xFF181920)
    val themeTextSecondary = if (isDark) Color.LightGray else Color(0xFF555866)

    // System AudioManager
    val audioManager = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    val maxSystemVolume = remember(audioManager) {
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
    }
    val currentSystemVolume = remember(audioManager) {
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }
    var volumeLevel by remember {
        mutableFloatStateOf(
            (currentSystemVolume.toFloat() / maxSystemVolume.toFloat()).coerceIn(0f, 1f)
        )
    }

    // Single ExoPlayer instance (Clean, robust, low-overhead with optimized MPD/DASH support)
    val exoPlayer = remember(context) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(20000)
            .setReadTimeoutMs(20000)

        // Konfigurasi buffer bufferDurationsMs optimal agar siaran MPD (DASH) & HLS berjalan stabil tanpa buffering berulang
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

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .build().apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        isPlaying = true
                        streamError = null
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                        isPlaying = false
                    }
                    Player.STATE_IDLE -> {
                        isBuffering = false
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                // Khusus untuk siaran MPD (DASH Live): otomatis sync ulang live window saat koneksi tertinggal
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    try {
                        exoPlayer.seekToDefaultPosition()
                        exoPlayer.prepare()
                        exoPlayer.play()
                        return
                    } catch (_: Exception) {}
                }

                isBuffering = false
                isPlaying = false
                streamError = "Siaran offline / format stream tidak dapat diputar."
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Sync volume - mute single player when multi-screen mode is active to prevent overlapping audio
    LaunchedEffect(volumeLevel, currentMultiScreenMode) {
        if (currentMultiScreenMode == MultiScreenMode.SINGLE) {
            exoPlayer.volume = volumeLevel
        } else {
            exoPlayer.volume = 0f
        }
    }

    // Initial channel load
    LaunchedEffect(Unit) {
        isLoadingChannels = true
        favoriteChannelIds = iptvRepository.getFavoriteChannelIds(context)
        val list = iptvRepository.fetchChannels(context)
        channels = list
        if (list.isNotEmpty()) {
            currentChannel = list.firstOrNull()
            // Multi-screen mode mewajibkan pengguna memilih channel masing-masing layar secara mandiri
            multiScreenChannels = listOf(null, null, null, null)
        }
        isLoadingChannels = false
    }

    // Load stream when currentChannel changes
    LaunchedEffect(currentChannel) {
        loadStream(currentChannel, exoPlayer)
    }

    val sortedChannels = remember(channels, currentSortMode) {
        when (currentSortMode) {
            ChannelSortMode.ORIGINAL -> channels
            ChannelSortMode.ASCENDING -> channels.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            ChannelSortMode.DESCENDING -> channels.sortedWith(compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name })
        }
    }

    val favoriteChannels = remember(sortedChannels, favoriteChannelIds, currentSortMode) {
        val favs = sortedChannels.filter { favoriteChannelIds.contains(it.id) }
        when (currentSortMode) {
            ChannelSortMode.ORIGINAL -> favs
            ChannelSortMode.ASCENDING -> favs.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            ChannelSortMode.DESCENDING -> favs.sortedWith(compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name })
        }
    }

    // Group channels by Genre with Favorit tab
    val groupedGenres = remember(sortedChannels, favoriteChannels, currentSortMode) {
        val map = mutableMapOf<String, List<IptvChannel>>()
        map["⭐ Favorit"] = favoriteChannels
        val normalGroups = sortedChannels.groupBy { it.genre.ifBlank { "Semua" } }
        normalGroups.forEach { (genre, list) ->
            map[genre] = when (currentSortMode) {
                ChannelSortMode.ORIGINAL -> list
                ChannelSortMode.ASCENDING -> list.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
                ChannelSortMode.DESCENDING -> list.sortedWith(compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name })
            }
        }
        map
    }
    val genreList = remember(groupedGenres) {
        groupedGenres.keys.toList()
    }

    var isReloading by remember { mutableStateOf(false) }

    val reloadChannels: () -> Unit = {
        scope.launch {
            isReloading = true
            val freshChannels = iptvRepository.fetchChannels(context)
            channels = freshChannels
            if (freshChannels.isNotEmpty()) {
                val updated = freshChannels.find { it.id == currentChannel?.id || it.name.equals(currentChannel?.name, ignoreCase = true) }
                    ?: freshChannels.firstOrNull()
                currentChannel = updated
            }
            isReloading = false
            val countGenres = freshChannels.groupBy { it.genre }.size
            Toast.makeText(
                context,
                "Berhasil update: ${freshChannels.size} channel & $countGenres genre",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val initialPageIndex = remember(sortedChannels, genreList) {
        val currentGenre = currentChannel?.genre ?: genreList.firstOrNull() ?: ""
        genreList.indexOf(currentGenre).coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(
        initialPage = initialPageIndex,
        pageCount = { genreList.size }
    )

    // Channel switching feedback state
    var channelSwitchFeedback by remember { mutableStateOf<String?>(null) }
    var feedbackJob by remember { mutableStateOf<Job?>(null) }

    val goToPreviousChannel: () -> Unit = {
        if (sortedChannels.isNotEmpty()) {
            val currentIndex = sortedChannels.indexOfFirst { it.id == currentChannel?.id }
            val prevIndex = if (currentIndex <= 0) sortedChannels.size - 1 else currentIndex - 1
            val target = sortedChannels[prevIndex]
            currentChannel = target
            exoPlayer.volume = volumeLevel
            exoPlayer.play()

            // Update genre tab jika channel baru berada di genre lain
            val genreIndex = genreList.indexOf(target.genre)
            if (genreIndex != -1 && pagerState.currentPage != genreIndex) {
                scope.launch { pagerState.animateScrollToPage(genreIndex) }
            }

            val feedbackText = "⏮ Channel Sebelumnya: ${target.name} (${prevIndex + 1}/${sortedChannels.size})"
            channelSwitchFeedback = feedbackText
            Toast.makeText(context, feedbackText, Toast.LENGTH_SHORT).show()

            feedbackJob?.cancel()
            feedbackJob = scope.launch {
                delay(2200L)
                channelSwitchFeedback = null
            }
        }
    }

    val goToNextChannel: () -> Unit = {
        if (sortedChannels.isNotEmpty()) {
            val currentIndex = sortedChannels.indexOfFirst { it.id == currentChannel?.id }
            val nextIndex = if (currentIndex >= sortedChannels.size - 1 || currentIndex == -1) 0 else currentIndex + 1
            val target = sortedChannels[nextIndex]
            currentChannel = target
            exoPlayer.volume = volumeLevel
            exoPlayer.play()

            // Update genre tab jika channel baru berada di genre lain
            val genreIndex = genreList.indexOf(target.genre)
            if (genreIndex != -1 && pagerState.currentPage != genreIndex) {
                scope.launch { pagerState.animateScrollToPage(genreIndex) }
            }

            val feedbackText = "⏭ Channel Selanjutnya: ${target.name} (${nextIndex + 1}/${sortedChannels.size})"
            channelSwitchFeedback = feedbackText
            Toast.makeText(context, feedbackText, Toast.LENGTH_SHORT).show()

            feedbackJob?.cancel()
            feedbackJob = scope.launch {
                delay(2200L)
                channelSwitchFeedback = null
            }
        }
    }

    val goToPreviousChannelForScreen: (Int) -> Unit = { screenIdx ->
        if (sortedChannels.isNotEmpty()) {
            val currentCh = multiScreenChannels.getOrNull(screenIdx)
            val currentIndex = if (currentCh != null) sortedChannels.indexOfFirst { it.id == currentCh.id } else -1
            val prevIndex = if (currentIndex <= 0) sortedChannels.size - 1 else currentIndex - 1
            val target = sortedChannels[prevIndex]

            val updated = multiScreenChannels.toMutableList()
            while (updated.size <= screenIdx) updated.add(null)
            updated[screenIdx] = target
            multiScreenChannels = updated
            if (screenIdx == 0) {
                currentChannel = target
            }

            val feedbackText = "Layar ${screenIdx + 1} ❮ Prev: ${target.name} (${prevIndex + 1}/${sortedChannels.size})"
            channelSwitchFeedback = feedbackText
            Toast.makeText(context, feedbackText, Toast.LENGTH_SHORT).show()

            feedbackJob?.cancel()
            feedbackJob = scope.launch {
                delay(2200L)
                channelSwitchFeedback = null
            }
        }
    }

    val goToNextChannelForScreen: (Int) -> Unit = { screenIdx ->
        if (sortedChannels.isNotEmpty()) {
            val currentCh = multiScreenChannels.getOrNull(screenIdx)
            val currentIndex = if (currentCh != null) sortedChannels.indexOfFirst { it.id == currentCh.id } else -1
            val nextIndex = if (currentIndex >= sortedChannels.size - 1 || currentIndex == -1) 0 else currentIndex + 1
            val target = sortedChannels[nextIndex]

            val updated = multiScreenChannels.toMutableList()
            while (updated.size <= screenIdx) updated.add(null)
            updated[screenIdx] = target
            multiScreenChannels = updated
            if (screenIdx == 0) {
                currentChannel = target
            }

            val feedbackText = "Layar ${screenIdx + 1} Next ❯: ${target.name} (${nextIndex + 1}/${sortedChannels.size})"
            channelSwitchFeedback = feedbackText
            Toast.makeText(context, feedbackText, Toast.LENGTH_SHORT).show()

            feedbackJob?.cancel()
            feedbackJob = scope.launch {
                delay(2200L)
                channelSwitchFeedback = null
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeBgColor)
            .testTag("live_streaming_screen")
    ) {
        // --- BAGIAN VIDEO STREAMING UTAMA (SELALU FULL LAYAR PENUH 100%) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (currentMultiScreenMode == MultiScreenMode.SINGLE) {
                // Single Video Player View
                SingleLivePlayerView(
                    channel = currentChannel,
                    player = exoPlayer,
                    isBuffering = isBuffering,
                    isPlaying = isPlaying,
                    streamError = streamError,
                    showControls = if (isFullScreen) showControlsInFullScreen else areControlsVisible,
                    onToggleControls = {
                        if (isFullScreen) {
                            showControlsInFullScreen = !showControlsInFullScreen
                            lastInteractionTime = System.currentTimeMillis()
                        } else {
                            toggleControlsVisibility()
                        }
                    },
                    onToggleFullScreen = {
                        if (isFullScreen) {
                            showControlsInFullScreen = !showControlsInFullScreen
                        } else {
                            isFullScreen = true
                        }
                    },
                    onUserInteraction = { recordUserInteraction() },
                    onPreviousChannel = { goToPreviousChannel() },
                    onNextChannel = { goToNextChannel() },
                    onOpenChannelPicker = {
                        pickerTargetScreenIndex = 0
                        recordUserInteraction()
                    },
                    channelSwitchFeedback = channelSwitchFeedback,
                    isFavorite = currentChannel?.let { favoriteChannelIds.contains(it.id) } ?: false,
                    onToggleFavorite = {
                        currentChannel?.let { ch -> toggleFavoriteChannel(ch.id) }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Multi Screen Player View (2 Layar: Atas & Bawah, atau 4 Layar: Grid 2x2)
                MultiScreenPlayerLayout(
                    mode = currentMultiScreenMode,
                    screenChannels = multiScreenChannels,
                    activeAudioIndex = activeAudioIndex,
                    volumeLevel = volumeLevel,
                    onSelectAudioIndex = { index ->
                        activeAudioIndex = index
                        recordUserInteraction()
                        Toast.makeText(context, "🔊 Audio Aktif: Layar ${index + 1}", Toast.LENGTH_SHORT).show()
                    },
                    onRequestChannelPicker = { index ->
                        pickerTargetScreenIndex = index
                        recordUserInteraction()
                    },
                    onPreviousChannel = { index ->
                        goToPreviousChannelForScreen(index)
                    },
                    onNextChannel = { index ->
                        goToNextChannelForScreen(index)
                    },
                    onToggleControls = {
                        toggleControlsVisibility()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Global Buffering indicator during channel repository fetch
            if (isLoadingChannels) {
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFFD700),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Mengambil siaran IPTV...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Tombol Melayang Cepat untuk Membuka Pilihan Channel & Kontrol saat Tersembunyi
        AnimatedVisibility(
            visible = !areControlsVisible && !isFullScreen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            Surface(
                onClick = {
                    toggleControlsVisibility()
                },
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.82f),
                border = BorderStroke(1.2.dp, themeGoldAccent.copy(alpha = 0.85f)),
                shadowElevation = 8.dp,
                modifier = Modifier.testTag("floating_open_channel_controls_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = themeGoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Pilihan Siaran & Kontrol",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // --- FORMAT MELAYANG / POP UP: PILIHAN CHANNEL DAN KELENGKAPANNYA (1 LAYAR & MULTI LAYAR) ---
        AnimatedVisibility(
            visible = !isFullScreen && areControlsVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = if (isLandscape) 14.dp else 8.dp)
                .padding(bottom = if (isLandscape) 6.dp else 10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = themeControlBarBg.copy(alpha = 0.96f),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        listOf(
                            themeGoldSoft,
                            themeGoldAccent,
                            themeGoldSoft
                        )
                    )
                ),
                shadowElevation = 16.dp,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("floating_channel_controls_popup")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp)
                ) {
                    // Header Pop-up Melayang: Judul dan Tombol Tutup (✕)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LiveTv,
                                contentDescription = null,
                                tint = themeGoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentMultiScreenMode == MultiScreenMode.SINGLE)
                                    "Pilihan Channel & Kontrol Siaran"
                                else
                                    "Pilihan Channel & Kontrol Multi Layar (${currentMultiScreenMode.title})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = themeTextColor
                            )
                        }

                        IconButton(
                            onClick = {
                                areControlsVisible = false
                                onControlsVisibilityChange(false)
                            },
                            modifier = Modifier
                                .size(26.dp)
                                .testTag("close_floating_controls_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup Pilihan Channel",
                                tint = themeTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Baris Pemilihan Target Layar (Khusus saat Mode Multi Layar Aktif)
                    if (currentMultiScreenMode != MultiScreenMode.SINGLE) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pilih Untuk:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = themeGoldAccent
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            (0 until currentMultiScreenMode.count).forEach { screenIdx ->
                                val isTarget = (activeAudioIndex == screenIdx)
                                val hasChannel = multiScreenChannels.getOrNull(screenIdx) != null
                                Surface(
                                    onClick = {
                                        activeAudioIndex = screenIdx
                                        recordUserInteraction()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isTarget) themeGoldAccent else (if (isDark) Color(0xFF222436) else Color(0xFFE2E6F0)),
                                    border = BorderStroke(
                                        0.8.dp,
                                        if (isTarget) themeGoldAccent else (if (isDark) Color(0xFF3B3E5B) else Color(0xFFCBD2E1))
                                    ),
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .testTag("target_screen_chip_${screenIdx + 1}")
                                ) {
                                    Text(
                                        text = "Layar ${screenIdx + 1}${if (hasChannel) " (${multiScreenChannels[screenIdx]?.name?.take(8) ?: "✓"})" else " (Kosong)"}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        fontWeight = if (isTarget) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isTarget) Color.Black else themeTextColor,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // --- BAGIAN KONTROL LENGKAP (7 TOMBOL UTAMA) ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                            // 1. Tombol Pemilihan Warna (Terang / Gelap / Default Perangkat)
                            Surface(
                                onClick = {
                                    recordUserInteraction()
                                    showThemeDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA),
                                border = BorderStroke(1.dp, themeGoldAccent.copy(alpha = 0.75f)),
                                modifier = Modifier.testTag("theme_selector_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (currentThemeMode) {
                                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                                            AppThemeMode.DARK -> Icons.Default.DarkMode
                                            AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                        },
                                        contentDescription = "Pilihan Warna",
                                        tint = themeGoldAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (currentThemeMode) {
                                            AppThemeMode.LIGHT -> "Terang"
                                            AppThemeMode.DARK -> "Gelap"
                                            AppThemeMode.SYSTEM -> "Sistem"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = themeTextColor
                                    )
                                }
                            }

                            // 2. Tombol Mode Orientasi Layar (Portait, Landscape, Automatic)
                            Surface(
                                onClick = {
                                    recordUserInteraction()
                                    showOrientationDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA),
                                border = BorderStroke(1.dp, themeGoldAccent.copy(alpha = 0.75f)),
                                modifier = Modifier.testTag("orientation_selector_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (currentOrientationMode) {
                                            AppOrientationMode.AUTO -> Icons.Default.ScreenRotation
                                            AppOrientationMode.PORTRAIT -> Icons.Default.StayCurrentPortrait
                                            AppOrientationMode.LANDSCAPE -> Icons.Default.StayCurrentLandscape
                                        },
                                        contentDescription = "Mode Orientasi Layar",
                                        tint = themeGoldAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = currentOrientationMode.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = themeTextColor
                                    )
                                }
                            }

                            // 3. Tombol Urutkan Channel (Ascending, Descending, Urutan Asli Playlist)
                            Surface(
                                onClick = {
                                    recordUserInteraction()
                                    showSortDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA),
                                border = BorderStroke(1.dp, themeGoldAccent.copy(alpha = 0.75f)),
                                modifier = Modifier.testTag("channel_sort_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (currentSortMode) {
                                            ChannelSortMode.ORIGINAL -> Icons.Default.FormatListNumbered
                                            ChannelSortMode.ASCENDING -> Icons.Default.ArrowUpward
                                            ChannelSortMode.DESCENDING -> Icons.Default.ArrowDownward
                                        },
                                        contentDescription = "Urutan Channel",
                                        tint = themeGoldAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = when (currentSortMode) {
                                            ChannelSortMode.ORIGINAL -> "Asli"
                                            ChannelSortMode.ASCENDING -> "A - Z"
                                            ChannelSortMode.DESCENDING -> "Z - A"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = themeTextColor
                                    )
                                }
                            }

                            // 4. Audio & Volume Button
                            Surface(
                                onClick = {
                                    recordUserInteraction()
                                    showAudioVolumeDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA),
                                border = BorderStroke(0.6.dp, if (isDark) Color(0xFF444760) else Color(0xFFCFD5E2)),
                                modifier = Modifier.testTag("audio_volume_button")
                            ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (volumeLevel == 0f) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = "Audio Volume",
                                    tint = if (volumeLevel == 0f) Color.Gray else themeGoldAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${(volumeLevel * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = themeTextColor
                                )
                            }
                        }

                        // 5. Tombol Mode Multi Layar (1 Layar, 2 Layar, 4 Layar)
                        Surface(
                            onClick = {
                                recordUserInteraction()
                                showMultiScreenDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA),
                            border = BorderStroke(1.dp, themeGoldAccent.copy(alpha = 0.75f)),
                            modifier = Modifier.testTag("multi_screen_mode_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (currentMultiScreenMode) {
                                        MultiScreenMode.SINGLE -> Icons.Default.Tv
                                        MultiScreenMode.DUAL -> Icons.Default.Splitscreen
                                        MultiScreenMode.QUAD -> Icons.Default.GridView
                                    },
                                    contentDescription = "Mode Multi Layar",
                                    tint = themeGoldAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = currentMultiScreenMode.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = themeTextColor
                                )
                            }
                        }

                        // 6. Reload Button
                        IconButton(
                            onClick = { reloadChannels() },
                            enabled = !isReloading,
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("reload_channels_button")
                        ) {
                            if (isReloading) {
                                CircularProgressIndicator(
                                    color = themeGoldAccent,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(15.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload Playlist",
                                    tint = themeGoldAccent,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        // 5. Playlist Settings Button
                        IconButton(
                            onClick = { showPlaylistSettingsSheet = true },
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("playlist_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Setting Playlist",
                                tint = themeTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // --- BAGIAN GENRE TABS & CHANNEL GRID (POP UP MELAYANG) ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(channelListHeight)
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeChannelListBg)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        // Genre Scrollable Tabs
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage.coerceIn(0, (genreList.size - 1).coerceAtLeast(0)),
                        edgePadding = 0.dp,
                        divider = {},
                        containerColor = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                    ) {
                        genreList.forEachIndexed { index, genreName ->
                            val isSelected = pagerState.currentPage == index
                            Tab(
                                selected = isSelected,
                                onClick = {
                                    recordUserInteraction()
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = {
                                    Text(
                                        text = genreName,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                        color = if (isSelected) themeGoldAccent else themeTextSecondary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Multi-Column Channel Grid with ~3-4 visible rows
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { page ->
                        val currentGenreName = genreList.getOrNull(page) ?: "Semua"
                        val genreChannels = groupedGenres[currentGenreName] ?: emptyList()

                        if (genreChannels.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (currentGenreName == "⭐ Favorit") "Belum ada channel favorit.\nKetuk ikon ❤️ pada channel untuk menambahkannya!"
                                           else "Tidak ada siaran dalam genre ini",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(columnCount),
                                contentPadding = PaddingValues(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(genreChannels, key = { it.id }) { ch ->
                                    val isCurrentChannel = currentChannel?.id == ch.id
                                    val isFav = favoriteChannelIds.contains(ch.id)

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                            .clickable {
                                                val now = System.currentTimeMillis()
                                                val isQuickDoubleTap = (lastClickedChannelId == ch.id) && (now - lastClickTimestamp < 650L)

                                                if (isQuickDoubleTap) {
                                                    // Ketuk 2x: Tampilkan Full Screen
                                                    isFullScreen = true
                                                    showControlsInFullScreen = false
                                                } else {
                                                    // Pilihan channel: Langsung putar channel live streaming & sembunyikan daftar pemilihan secara otomatis saat itu juga
                                                    lastClickedChannelId = ch.id
                                                    lastClickTimestamp = now

                                                    if (currentMultiScreenMode == MultiScreenMode.SINGLE) {
                                                        currentChannel = ch
                                                        exoPlayer.volume = volumeLevel
                                                        exoPlayer.play()
                                                    } else {
                                                        val targetIdx = activeAudioIndex.coerceIn(0, currentMultiScreenMode.count - 1)
                                                        val updated = multiScreenChannels.toMutableList()
                                                        while (updated.size <= targetIdx) {
                                                            updated.add(null)
                                                        }
                                                        updated[targetIdx] = ch
                                                        multiScreenChannels = updated
                                                        if (targetIdx == 0) {
                                                            currentChannel = ch
                                                        }
                                                        Toast.makeText(context, "Layar ${targetIdx + 1}: ${ch.name}", Toast.LENGTH_SHORT).show()
                                                    }

                                                    // Saat pengguna telah memilih channel, secara otomatis saat itu juga semua kontrol dan list sembunyi
                                                    areControlsVisible = false
                                                    showControlsInFullScreen = false
                                                    onControlsVisibilityChange(false)
                                                }
                                            }
                                            .testTag("channel_item_${ch.id}"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCurrentChannel) themeCardSelectedBg else themeCardBg
                                        ),
                                        border = if (isCurrentChannel) BorderStroke(1.8.dp, themeGoldAccent)
                                                 else BorderStroke(0.6.dp, if (isDark) Color(0xFF2E3040) else Color(0xFFD4D8E2))
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 3.dp, vertical = 2.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (isCurrentChannel) themeGoldAccent.copy(alpha = 0.2f)
                                                            else if (isDark) Color(0xFF252636) else Color(0xFFEEF0F6)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (!ch.logoUrl.isNullOrEmpty()) {
                                                        AsyncImage(
                                                            model = ch.logoUrl,
                                                            contentDescription = ch.name,
                                                            contentScale = ContentScale.Fit,
                                                            error = painterResource(id = R.drawable.img_app_icon_1784738562791),
                                                            fallback = painterResource(id = R.drawable.img_app_icon_1784738562791),
                                                            modifier = Modifier
                                                                .size(26.dp)
                                                                .clip(RoundedCornerShape(5.dp))
                                                        )
                                                    } else {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.img_app_icon_1784738562791),
                                                            contentDescription = ch.name,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .size(26.dp)
                                                                .clip(RoundedCornerShape(5.dp))
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(1.dp))

                                                Text(
                                                    text = ch.name,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                                    fontWeight = if (isCurrentChannel) FontWeight.ExtraBold else FontWeight.Medium,
                                                    color = if (isCurrentChannel) themeGoldAccent else themeTextColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            if (ch.isMpd || ch.streamUrl.lowercase().contains(".mpd")) {
                                                Surface(
                                                    color = Color(0xDD332912),
                                                    border = BorderStroke(0.5.dp, Color(0xFFFFD700)),
                                                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 6.dp),
                                                    modifier = Modifier.align(Alignment.TopStart)
                                                ) {
                                                    Text(
                                                        text = "MPD",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.5.sp),
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFFFFD700),
                                                        modifier = Modifier.padding(horizontal = 2.5.dp, vertical = 0.5.dp)
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = {
                                                    toggleFavoriteChannel(ch.id)
                                                },
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .align(Alignment.TopEnd)
                                                    .testTag("fav_btn_${ch.id}")
                                            ) {
                                                Icon(
                                                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "Favorit",
                                                    tint = if (isFav) Color(0xFFFF4757) else themeTextSecondary.copy(alpha = 0.45f),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Floating Minimalist Luxury Controls Overlay in FULL SCREEN MODE (Toggle on tap)
    if (isFullScreen) {
            AnimatedVisibility(
                visible = showControlsInFullScreen,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 12.dp, end = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xEE141520),
                    border = BorderStroke(1.2.dp, Color(0xFFFFD700)),
                    shadowElevation = 12.dp,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Exit Full Screen Button
                        Surface(
                            onClick = { isFullScreen = false },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF332912),
                            border = BorderStroke(1.dp, Color(0xFFFFD700)),
                            modifier = Modifier.testTag("exit_fullscreen_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FullscreenExit,
                                    contentDescription = "Layar Normal",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Normal",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }

                        // Theme Mode Selector in Fullscreen
                        Surface(
                            onClick = { showThemeDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E202B),
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.8f)),
                            modifier = Modifier.testTag("fullscreen_theme_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (currentThemeMode) {
                                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                                        AppThemeMode.DARK -> Icons.Default.DarkMode
                                        AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                    },
                                    contentDescription = "Pilihan Warna",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (currentThemeMode) {
                                        AppThemeMode.LIGHT -> "Terang"
                                        AppThemeMode.DARK -> "Gelap"
                                        AppThemeMode.SYSTEM -> "Sistem"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Orientation Mode Selector in Fullscreen
                        Surface(
                            onClick = {
                                recordUserInteraction()
                                showOrientationDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E202B),
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.8f)),
                            modifier = Modifier.testTag("fullscreen_orientation_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (currentOrientationMode) {
                                        AppOrientationMode.AUTO -> Icons.Default.ScreenRotation
                                        AppOrientationMode.PORTRAIT -> Icons.Default.StayCurrentPortrait
                                        AppOrientationMode.LANDSCAPE -> Icons.Default.StayCurrentLandscape
                                    },
                                    contentDescription = "Mode Orientasi",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentOrientationMode.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Channel Sort Selector in Fullscreen
                        Surface(
                            onClick = {
                                recordUserInteraction()
                                showSortDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E202B),
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.8f)),
                            modifier = Modifier.testTag("fullscreen_sort_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (currentSortMode) {
                                        ChannelSortMode.ORIGINAL -> Icons.Default.FormatListNumbered
                                        ChannelSortMode.ASCENDING -> Icons.Default.ArrowUpward
                                        ChannelSortMode.DESCENDING -> Icons.Default.ArrowDownward
                                    },
                                    contentDescription = "Urutan Channel",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (currentSortMode) {
                                        ChannelSortMode.ORIGINAL -> "Asli"
                                        ChannelSortMode.ASCENDING -> "A - Z"
                                        ChannelSortMode.DESCENDING -> "Z - A"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dialog Pemilihan Mode Orientasi Layar (Portait / Landscape / Automatic)
        if (showOrientationDialog) {
            Dialog(
                onDismissRequest = {
                    recordUserInteraction()
                    showOrientationDialog = false
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .testTag("orientation_selection_dialog"),
                        shape = RoundedCornerShape(24.dp),
                        color = themeControlBarBg,
                        border = BorderStroke(1.4.dp, themeGoldAccent),
                        tonalElevation = 10.dp,
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ScreenRotation,
                                        contentDescription = null,
                                        tint = themeGoldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Pilih Mode Layar",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = themeTextColor
                                    )
                                }
                                IconButton(onClick = {
                                    recordUserInteraction()
                                    showOrientationDialog = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = themeTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3 Orientation Options
                            listOf(
                                Triple(AppOrientationMode.PORTRAIT, Icons.Default.StayCurrentPortrait, "Mode Portait"),
                                Triple(AppOrientationMode.LANDSCAPE, Icons.Default.StayCurrentLandscape, "Mode Landscape"),
                                Triple(AppOrientationMode.AUTO, Icons.Default.ScreenRotation, "Automatic (Sesuai Device)")
                            ).forEach { (mode, icon, label) ->
                                val isSelected = currentOrientationMode == mode
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            applyOrientation(mode)
                                            recordUserInteraction()
                                            showOrientationDialog = false
                                        }
                                        .testTag("orientation_option_${mode.name.lowercase()}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            if (isDark) Color(0xFF332912) else Color(0xFFFFF6DF)
                                        } else {
                                            if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA)
                                        }
                                    ),
                                    border = if (isSelected) BorderStroke(1.5.dp, themeGoldAccent)
                                             else BorderStroke(0.6.dp, if (isDark) Color(0xFF33354A) else Color(0xFFD6DBE6))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) themeGoldAccent.copy(alpha = 0.2f)
                                                        else if (isDark) Color(0xFF252636) else Color(0xFFE2E6F0)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) themeGoldAccent else themeTextSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                    color = if (isSelected) themeGoldAccent else themeTextColor
                                                )
                                                Text(
                                                    text = mode.subtitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = themeTextSecondary
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Dipilih",
                                                tint = themeGoldAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Theme Selection Dialog (Terang / Gelap / Default Perangkat)
        if (showThemeDialog) {
            Dialog(
                onDismissRequest = { showThemeDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .testTag("theme_selection_dialog"),
                        shape = RoundedCornerShape(24.dp),
                        color = themeControlBarBg,
                        border = BorderStroke(1.4.dp, themeGoldAccent),
                        tonalElevation = 10.dp,
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BrightnessAuto,
                                        contentDescription = null,
                                        tint = themeGoldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Pilih Tampilan Warna",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = themeTextColor
                                    )
                                }
                                IconButton(onClick = { showThemeDialog = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = themeTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3 Theme Options Cards
                            listOf(
                                Triple(AppThemeMode.LIGHT, Icons.Default.LightMode, "Warna Terang"),
                                Triple(AppThemeMode.DARK, Icons.Default.DarkMode, "Warna Gelap"),
                                Triple(AppThemeMode.SYSTEM, Icons.Default.BrightnessAuto, "Default Perangkat")
                            ).forEach { (mode, icon, label) ->
                                val isSelected = currentThemeMode == mode
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            onThemeModeChange(mode)
                                            AppThemeMode.saveToPreferences(context, mode)
                                            showThemeDialog = false
                                        }
                                        .testTag("theme_option_${mode.name.lowercase()}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            if (isDark) Color(0xFF332912) else Color(0xFFFFF6DF)
                                        } else {
                                            if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA)
                                        }
                                    ),
                                    border = if (isSelected) BorderStroke(1.5.dp, themeGoldAccent)
                                             else BorderStroke(0.6.dp, if (isDark) Color(0xFF33354A) else Color(0xFFD6DBE6))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) themeGoldAccent.copy(alpha = 0.2f)
                                                        else if (isDark) Color(0xFF252636) else Color(0xFFE2E6F0)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) themeGoldAccent else themeTextSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                    color = if (isSelected) themeGoldAccent else themeTextColor
                                                )
                                                Text(
                                                    text = mode.subtitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = themeTextSecondary
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Dipilih",
                                                tint = themeGoldAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog Pemilihan Urutan Channel (Ascending, Descending, Urutan Asli Playlist)
        if (showSortDialog) {
            Dialog(
                onDismissRequest = {
                    recordUserInteraction()
                    showSortDialog = false
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .testTag("channel_sort_dialog"),
                        shape = RoundedCornerShape(24.dp),
                        color = themeControlBarBg,
                        border = BorderStroke(1.4.dp, themeGoldAccent),
                        tonalElevation = 10.dp,
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SortByAlpha,
                                        contentDescription = null,
                                        tint = themeGoldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Urutkan Channel",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = themeTextColor
                                    )
                                }
                                IconButton(onClick = {
                                    recordUserInteraction()
                                    showSortDialog = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = themeTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3 Sort Options with Icons
                            listOf(
                                Triple(ChannelSortMode.ORIGINAL, Icons.Default.FormatListNumbered, "Urutan Asli Playlist"),
                                Triple(ChannelSortMode.ASCENDING, Icons.Default.ArrowUpward, "Urutan A - Z (Ascending)"),
                                Triple(ChannelSortMode.DESCENDING, Icons.Default.ArrowDownward, "Urutan Z - A (Descending)")
                            ).forEach { (mode, icon, label) ->
                                val isSelected = currentSortMode == mode
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            currentSortMode = mode
                                            iptvRepository.saveSortMode(context, mode)
                                            recordUserInteraction()
                                            showSortDialog = false
                                            Toast.makeText(context, "Daftar diurutkan: $label", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("sort_option_${mode.name.lowercase()}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            if (isDark) Color(0xFF332912) else Color(0xFFFFF6DF)
                                        } else {
                                            if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA)
                                        }
                                    ),
                                    border = if (isSelected) BorderStroke(1.5.dp, themeGoldAccent)
                                             else BorderStroke(0.6.dp, if (isDark) Color(0xFF33354A) else Color(0xFFD6DBE6))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) themeGoldAccent.copy(alpha = 0.2f)
                                                        else if (isDark) Color(0xFF252636) else Color(0xFFE2E6F0)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) themeGoldAccent else themeTextSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                    color = if (isSelected) themeGoldAccent else themeTextColor
                                                )
                                                Text(
                                                    text = mode.subtitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = themeTextSecondary
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Dipilih",
                                                tint = themeGoldAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Audio Volume Slider Dialog / Popup
        if (showAudioVolumeDialog) {
            Dialog(
                onDismissRequest = { showAudioVolumeDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(top = 16.dp, bottom = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .testTag("audio_volume_popup"),
                        shape = RoundedCornerShape(24.dp),
                        color = themeControlBarBg,
                        border = BorderStroke(1.2.dp, themeGoldAccent),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = themeGoldAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Pengaturan Volume Audio",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = themeTextColor
                                    )
                                }
                                IconButton(onClick = { showAudioVolumeDialog = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = themeTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    volumeLevel = 0f
                                    val targetVolIndex = 0
                                    try { audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolIndex, 0) } catch (_: Exception) {}
                                    exoPlayer.volume = 0f
                                }) {
                                    Icon(
                                        imageVector = if (volumeLevel == 0f) Icons.Default.VolumeOff else Icons.Default.VolumeMute,
                                        contentDescription = "Mute",
                                        tint = Color.Gray
                                    )
                                }

                                Slider(
                                    value = volumeLevel,
                                    onValueChange = { newVol ->
                                        volumeLevel = newVol
                                        val targetVolIndex = (newVol * maxSystemVolume).toInt().coerceIn(0, maxSystemVolume)
                                        try {
                                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolIndex, 0)
                                        } catch (_: Exception) {}
                                        exoPlayer.volume = newVol
                                    },
                                    valueRange = 0f..1f,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(onClick = {
                                    volumeLevel = 1f
                                    val targetVolIndex = maxSystemVolume
                                    try { audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolIndex, 0) } catch (_: Exception) {}
                                    exoPlayer.volume = 1f
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Max Volume",
                                        tint = themeGoldAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Volume Perangkat: ${(volumeLevel * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = themeGoldAccent,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Playlist Settings Sheet
        if (showPlaylistSettingsSheet) {
            PlaylistSettingsBottomSheet(
                iptvRepository = iptvRepository,
                onReloadChannels = {
                    scope.launch {
                        isLoadingChannels = true
                        val newChannels = iptvRepository.fetchChannels(context)
                        channels = newChannels
                        if (newChannels.isNotEmpty()) {
                            currentChannel = newChannels.firstOrNull()
                        }
                        isLoadingChannels = false
                    }
                },
                onDismiss = { showPlaylistSettingsSheet = false }
            )
        }

        // Dialog Pemilihan Mode Multi Layar (1 Layar, 2 Layar, 4 Layar)
        if (showMultiScreenDialog) {
            MultiScreenModeDialog(
                currentMode = currentMultiScreenMode,
                onModeSelected = { newMode ->
                    val oldMode = currentMultiScreenMode
                    currentMultiScreenMode = newMode
                    if (newMode != MultiScreenMode.SINGLE && newMode != oldMode) {
                        // Pengguna diwajibkan memilih channel masing-masing layar secara mandiri
                        multiScreenChannels = listOf(null, null, null, null)
                        activeAudioIndex = 0
                        // Buka otomatis kotak pilihan channel untuk Layar 1
                        pickerTargetScreenIndex = 0
                    }
                    recordUserInteraction()
                    Toast.makeText(
                        context,
                        if (newMode == MultiScreenMode.SINGLE) "Mode 1 Layar Aktif"
                        else "Mode ${newMode.title} Aktif: Silakan pilih channel untuk tiap layar",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onDismiss = { showMultiScreenDialog = false },
                isDark = isDark
            )
        }

        // Kotak Pop Up Pemilihan Channel untuk Setiap Layar
        if (pickerTargetScreenIndex != null) {
            val targetIdx = pickerTargetScreenIndex!!
            ChannelPickerPopupDialog(
                targetScreenName = "Layar ${targetIdx + 1}",
                currentChannelId = multiScreenChannels.getOrNull(targetIdx)?.id,
                channels = sortedChannels,
                favoriteChannelIds = favoriteChannelIds,
                onSelectChannel = { selectedChannel ->
                    val updated = multiScreenChannels.toMutableList()
                    while (updated.size <= targetIdx) {
                        updated.add(null)
                    }
                    updated[targetIdx] = selectedChannel
                    multiScreenChannels = updated
                    if (targetIdx == 0) {
                        currentChannel = selectedChannel
                    }
                    val nextEmptyIdx = (0 until currentMultiScreenMode.count).firstOrNull { idx ->
                        idx != targetIdx && multiScreenChannels.getOrNull(idx) == null
                    }
                    recordUserInteraction()
                    Toast.makeText(context, "Layar ${targetIdx + 1} memutar: ${selectedChannel.name}", Toast.LENGTH_SHORT).show()

                    // Otomatis pandu pengguna memilih channel untuk layar berikutnya yang masih kosong
                    if (nextEmptyIdx != null) {
                        pickerTargetScreenIndex = nextEmptyIdx
                    } else {
                        pickerTargetScreenIndex = null
                    }
                },
                onDismiss = { pickerTargetScreenIndex = null },
                isDark = isDark
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format(java.util.Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    }
}

@Composable
private fun SingleLivePlayerView(
    channel: IptvChannel?,
    player: ExoPlayer,
    isBuffering: Boolean,
    isPlaying: Boolean,
    streamError: String?,
    showControls: Boolean,
    onToggleControls: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onUserInteraction: () -> Unit,
    onPreviousChannel: () -> Unit = {},
    onNextChannel: () -> Unit = {},
    onOpenChannelPicker: () -> Unit = {},
    channelSwitchFeedback: String? = null,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isDraggingSeek by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(player, isPlaying) {
        while (true) {
            try {
                if (player.isPlaying && !isDraggingSeek) {
                    currentPosition = player.currentPosition
                    val dur = player.duration
                    if (dur > 0 && dur != androidx.media3.common.C.TIME_UNSET) {
                        duration = dur
                    }
                }
            } catch (_: Exception) {}
            delay(500L)
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .testTag("single_live_player_view"),
        contentAlignment = Alignment.Center
    ) {
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

        // Touch overlay:
        // - Single tap: Munculkan / sembunyikan tombol kontrol
        // - Double tap tengah: Munculkan kotak pop up pemilihan channel
        // - Double tap kanan/kiri atas: Ganti channel sebelumnya
        // - Double tap kanan/kiri bawah: Ganti channel selanjutnya
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(onPreviousChannel, onNextChannel, onToggleControls, onOpenChannelPicker) {
                    detectTapGestures(
                        onTap = {
                            onToggleControls()
                        },
                        onDoubleTap = { offset ->
                            val isCenterX = offset.x >= (size.width * 0.22f) && offset.x <= (size.width * 0.78f)
                            val isCenterY = offset.y >= (size.height * 0.22f) && offset.y <= (size.height * 0.78f)
                            if (isCenterX && isCenterY) {
                                // Ketuk 2x di Tengah Layar: Otomatis munculkan kotak pop up pemilihan channel
                                onOpenChannelPicker()
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
                .testTag("live_player_touch_overlay")
        )

        // Visual Notification Badge saat ganti channel via double tap
        AnimatedVisibility(
            visible = !channelSwitchFeedback.isNullOrEmpty(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.88f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.4.dp, Color(0xFFFFD700)),
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = channelSwitchFeedback ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Subtle gradient overlay for readability
        if (showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )
        }

        // Judul Channel Biasa (Tanpa Kotak Warna) & Petunjuk Ketuk 2x
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (channel != null) {
                    if (!channel.logoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = channel.logoUrl,
                            contentDescription = channel.name,
                            contentScale = ContentScale.Fit,
                            error = painterResource(id = R.drawable.img_app_icon_1784738562791),
                            fallback = painterResource(id = R.drawable.img_app_icon_1784738562791),
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon_1784738562791),
                            contentDescription = channel.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = channel?.name ?: "Pilih Siaran Channel",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (channel != null && (channel.isMpd || channel.streamUrl.lowercase().contains(".mpd"))) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xDD332912),
                        border = BorderStroke(0.8.dp, Color(0xFFFFD700)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "MPD/DASH",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp),
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                if (channel != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { onToggleFavorite() },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("player_favorite_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritkan Channel",
                            tint = if (isFavorite) Color(0xFFFF4757) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (showControls) {
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.6.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "Ketuk 2x Tengah: Pop Up Channel | Kanan/Kiri Atas: ❮ Prev | Kanan/Kiri Bawah: Next ❯",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Center prompt if no channel selected
        if (channel == null) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, Color(0xFFFFD700))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Silakan pilih siaran channel di bawah",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Buffering Indicator
        if (isBuffering) {
            CircularProgressIndicator(
                color = Color(0xFFFFD700),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }

        // Stream Error Message
        if (streamError != null && channel != null) {
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFF5252)),
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Siaran offline / gagal dimuat",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF6B81),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Tombol Control Video: Muncul untuk Mempercepat / Memperlambat dengan menggeser titik
        if (showControls && channel != null) {
            Surface(
                color = Color.Black.copy(alpha = 0.78f),
                shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .testTag("video_control_bar")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    // Baris 1: Play/Pause, Rewind 10s, Forward 10s, Waktu & Seekbar (Geser Titik Posisi)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                onUserInteraction()
                                if (player.isPlaying) player.pause() else player.play()
                            },
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("video_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                onUserInteraction()
                                val target = (player.currentPosition - 10000L).coerceAtLeast(0L)
                                player.seekTo(target)
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("video_rewind_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Mundur 10 detik",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                onUserInteraction()
                                val target = player.currentPosition + 10000L
                                player.seekTo(target)
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("video_forward_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Maju 10 detik",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = if (duration > 0) formatTime(if (isDraggingSeek) seekPosition.toLong() else currentPosition) else "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (duration > 0) Color.White else Color(0xFFFF5252),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )

                        if (duration > 0) {
                            Slider(
                                value = if (isDraggingSeek) seekPosition else currentPosition.toFloat().coerceIn(0f, duration.toFloat()),
                                onValueChange = {
                                    onUserInteraction()
                                    isDraggingSeek = true
                                    seekPosition = it
                                },
                                onValueChangeFinished = {
                                    onUserInteraction()
                                    player.seekTo(seekPosition.toLong())
                                    isDraggingSeek = false
                                },
                                valueRange = 0f..duration.toFloat(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .testTag("video_seek_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFFD700),
                                    activeTrackColor = Color(0xFFFFD700),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                                )
                            )

                            Text(
                                text = formatTime(duration),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    // Baris 2: Kontrol Kecepatan Video (Geser Titik untuk Mempercepat / Memperlambat)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Kecepatan",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = "Kecepatan: ${String.format(java.util.Locale.US, "%.2f", playbackSpeed)}x",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )

                        Slider(
                            value = playbackSpeed,
                            onValueChange = { newSpeed ->
                                onUserInteraction()
                                playbackSpeed = newSpeed
                                player.setPlaybackSpeed(newSpeed)
                            },
                            valueRange = 0.5f..2.0f,
                            steps = 5,
                            modifier = Modifier
                                .weight(1f)
                                .height(24.dp)
                                .testTag("video_speed_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFFFFD700),
                                inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                            )
                        )

                        if (playbackSpeed != 1.0f) {
                            Surface(
                                onClick = {
                                    onUserInteraction()
                                    playbackSpeed = 1.0f
                                    player.setPlaybackSpeed(1.0f)
                                },
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0x33FFD700),
                                border = BorderStroke(0.6.dp, Color(0xFFFFD700))
                            ) {
                                Text(
                                    text = "1x",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun hexToBytes(hex: String): ByteArray {
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

private fun buildClearKeyDataUri(kidHex: String, keyHex: String): Uri? {
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

private fun loadStream(channel: IptvChannel?, player: ExoPlayer) {
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

        // Build data source factory dengan User-Agent & header khusus playlist
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
            // Set format MPEG-DASH (MPD)
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)

            // Konfigurasi live playback sync untuk MPD agar tidak lagging dan stabil
            val liveConfig = MediaItem.LiveConfiguration.Builder()
                .setTargetOffsetMs(6000L)
                .setMinOffsetMs(2500L)
                .setMaxOffsetMs(25000L)
                .setMinPlaybackSpeed(0.97f)
                .setMaxPlaybackSpeed(1.03f)
                .build()
            mediaItemBuilder.setLiveConfiguration(liveConfig)

            // Dukungan ClearKey DRM & Widevine jika source playlist menyertakan kunci lisensi
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
                // Gunakan DashMediaSource.Factory dengan chunk source dedicated
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistSettingsBottomSheet(
    iptvRepository: IptvRepository,
    onReloadChannels: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var playlistSources by remember { mutableStateOf(iptvRepository.getSavedPlaylistSources(context)) }
    var newUrlInput by remember { mutableStateOf("") }
    var newCustomText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Daftar Source Playlist, 1: Tambah Source / File M3U

    // Activity launcher untuk memilih file M3U dari penyimpanan lokal device
    val m3uFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            var fileName = "File M3U Lokal"
            try {
                context.contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        val fetched = cursor.getString(nameIndex)
                        if (!fetched.isNullOrBlank()) {
                            fileName = fetched
                        }
                    }
                }
            } catch (_: Exception) {}

            val fileContent = try {
                context.contentResolver.openInputStream(fileUri)?.bufferedReader()?.use { it.readText() } ?: ""
            } catch (_: Exception) {
                ""
            }

            if (fileContent.isNotBlank()) {
                val newSource = PlaylistSource(
                    id = "local_file_${System.currentTimeMillis()}",
                    name = fileName,
                    urlOrContent = fileContent,
                    type = PlaylistSourceType.LOCAL_FILE,
                    isEnabled = true
                )
                iptvRepository.addPlaylistSource(context, newSource)
                playlistSources = iptvRepository.getSavedPlaylistSources(context)
                Toast.makeText(context, "Berhasil menambahkan $fileName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal membaca isi file M3U", Toast.LENGTH_SHORT).show()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("playlist_settings_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Pengaturan Playlist IPTV",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_playlist_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Daftar Source (${playlistSources.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_source_list")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Tambah Source / File M3U", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_add_source")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                Text(
                    text = "Pilih source playlist yang akan dipakai sebagai bahan materi pemilihan channel:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlistSources, key = { it.id }) { source ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (source.isEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (source.isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("source_card_${source.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = when (source.type) {
                                            PlaylistSourceType.URL -> Icons.Default.Link
                                            PlaylistSourceType.LOCAL_FILE -> Icons.Default.FolderOpen
                                            PlaylistSourceType.CUSTOM_TEXT -> Icons.Default.EditNote
                                        },
                                        contentDescription = null,
                                        tint = if (source.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = source.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = when (source.type) {
                                                PlaylistSourceType.URL -> source.urlOrContent
                                                PlaylistSourceType.LOCAL_FILE -> "File lokal dari perangkat"
                                                PlaylistSourceType.CUSTOM_TEXT -> "Teks M3U Manual"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Toggle Switch untuk Dipakai / Tidak Dipakai
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = if (source.isEnabled) "Dipakai" else "Tidak",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = if (source.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        )
                                        Switch(
                                            checked = source.isEnabled,
                                            onCheckedChange = { isChecked ->
                                                iptvRepository.togglePlaylistSourceEnabled(context, source.id, isChecked)
                                                playlistSources = iptvRepository.getSavedPlaylistSources(context)
                                            },
                                            modifier = Modifier
                                                .size(width = 38.dp, height = 24.dp)
                                                .testTag("toggle_source_${source.id}")
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            iptvRepository.removePlaylistSource(context, source.id)
                                            playlistSources = iptvRepository.getSavedPlaylistSources(context)
                                        },
                                        modifier = Modifier.testTag("delete_source_${source.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Source",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val preset1 = PlaylistSource(
                                id = "preset_adult_1_${System.currentTimeMillis()}",
                                name = "Adult IPTV Source 1",
                                urlOrContent = "https://raw.githubusercontent.com/atsushi444/iptv/refs/heads/main/adult.m3u",
                                type = PlaylistSourceType.URL,
                                isEnabled = true
                            )
                            iptvRepository.addPlaylistSource(context, preset1)
                            playlistSources = iptvRepository.getSavedPlaylistSources(context)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Adult 1", color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = {
                            val preset2 = PlaylistSource(
                                id = "preset_adult_2_${System.currentTimeMillis()}",
                                name = "Adult IPTV Source 2",
                                urlOrContent = "https://raw.githubusercontent.com/atsushi444/iptv/refs/heads/main/adult2.m3u",
                                type = PlaylistSourceType.URL,
                                isEnabled = true
                            )
                            iptvRepository.addPlaylistSource(context, preset2)
                            playlistSources = iptvRepository.getSavedPlaylistSources(context)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Adult 2", color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.labelSmall)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        // 1. Pilih File M3U dari Device Storage
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "1. Impor File M3U dari Perangkat",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pilih file .m3u / .m3u8 / .txt yang tersimpan di memori perangkat HP/Tablet Anda.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        m3uFilePickerLauncher.launch("*/*")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("pick_m3u_file_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pilih File M3U dari Perangkat", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        // 2. URL Playlist Baru
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "2. Tambah URL Source M3U",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = newUrlInput,
                                        onValueChange = { newUrlInput = it },
                                        placeholder = { Text("https://example.com/playlist.m3u") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (newUrlInput.isNotBlank()) {
                                                val src = PlaylistSource(
                                                    id = "url_${System.currentTimeMillis()}",
                                                    name = "URL ${newUrlInput.takeLast(20)}",
                                                    urlOrContent = newUrlInput.trim(),
                                                    type = PlaylistSourceType.URL,
                                                    isEnabled = true
                                                )
                                                iptvRepository.addPlaylistSource(context, src)
                                                playlistSources = iptvRepository.getSavedPlaylistSources(context)
                                                newUrlInput = ""
                                                Toast.makeText(context, "URL berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // 3. Teks M3U Manual
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "3. Tempel Teks M3U Manual",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = newCustomText,
                                    onValueChange = { newCustomText = it },
                                    placeholder = { Text("#EXTM3U\n#EXTINF:-1 group-title=\"Live\", Nama Channel\nhttp://.../stream.m3u8") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (newCustomText.isNotBlank()) {
                                            val src = PlaylistSource(
                                                id = "custom_text_${System.currentTimeMillis()}",
                                                name = "Teks M3U Manual",
                                                urlOrContent = newCustomText,
                                                type = PlaylistSourceType.CUSTOM_TEXT,
                                                isEnabled = true
                                            )
                                            iptvRepository.addPlaylistSource(context, src)
                                            playlistSources = iptvRepository.getSavedPlaylistSources(context)
                                            newCustomText = ""
                                            Toast.makeText(context, "Teks M3U berhasil disimpan", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Simpan Teks M3U", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        iptvRepository.resetPlaylists(context)
                        playlistSources = iptvRepository.getSavedPlaylistSources(context)
                        newUrlInput = ""
                        newCustomText = ""
                        Toast.makeText(context, "Playlist direset ke default", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("reset_playlist_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Default")
                }

                Button(
                    onClick = {
                        onReloadChannels()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("apply_and_reload_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Terapkan & Reload", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
