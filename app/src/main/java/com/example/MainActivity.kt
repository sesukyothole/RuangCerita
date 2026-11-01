package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.data.ReaderTheme
import com.example.ui.MainViewModel
import com.example.ui.components.GreetingOverlay
import com.example.ui.components.ReaderSettingsSheet
import com.example.ui.components.StoryPinDialog
import com.example.ui.components.DashboardPinDialog
import com.example.ui.components.AdminChangePinDialog
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LiveStreamingScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.RuangCeritaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        setContent {
            val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDark = when (appThemeMode) {
                com.example.data.AppThemeMode.SYSTEM -> isSystemDark
                com.example.data.AppThemeMode.LIGHT -> false
                com.example.data.AppThemeMode.DARK -> true
            }

            RuangCeritaTheme(darkTheme = isDark) {
                RuangCeritaApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onStop() {
        super.onStop()
        showSystemBars()
    }

    override fun onDestroy() {
        super.onDestroy()
        showSystemBars()
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun showSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun RuangCeritaApp(viewModel: MainViewModel) {
    val showWelcomeBanner by viewModel.showWelcomeBanner.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val favoriteStories by viewModel.favoriteStories.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val preferences by viewModel.readerPreferences.collectAsStateWithLifecycle()
    val currentStoryId by viewModel.currentStoryId.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val systemIsDark = isSystemInDarkTheme()
    val isDark = when (appThemeMode) {
        com.example.data.AppThemeMode.SYSTEM -> systemIsDark
        com.example.data.AppThemeMode.LIGHT -> false
        com.example.data.AppThemeMode.DARK -> true
    }

    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var isLiveTvControlsVisible by remember { mutableStateOf(true) }
    var showGlobalReaderSettingsSheet by remember { mutableStateOf(false) }
    var isAgeVerifiedForDashboard by remember { mutableStateOf(false) }
    var showPinDialogForDashboard by remember { mutableStateOf(false) }
    var showAdminChangePinDialog by remember { mutableStateOf(false) }

    val activeStory = remember(currentStoryId, stories, favoriteStories) {
        currentStoryId?.let { id ->
            stories.find { it.id == id } ?: favoriteStories.find { it.id == id }
        }
    }

    // 1. Initial Greeting Overlay Screen ("Ketika aplikasi dibuka muncul ucapan selamat datang...")
    if (showWelcomeBanner) {
        Box(modifier = Modifier.fillMaxSize()) {
            GreetingOverlay(
                onEnterDashboard = {
                    showPinDialogForDashboard = true
                }
            )

            // Dialog Verifikasi PIN Usia Dewasa (17+) sewaktu akan masuk ke dashboard
            if (showPinDialogForDashboard) {
                DashboardPinDialog(
                    isDark = isDark,
                    onDismiss = {
                        (context as? Activity)?.finish()
                    },
                    onPinCorrect = {
                        isAgeVerifiedForDashboard = true
                        showPinDialogForDashboard = false
                        selectedBottomTab = 0
                        viewModel.dismissWelcomeBanner()
                    },
                    onOpenAdminChangePin = {
                        showAdminChangePinDialog = true
                    }
                )
            }

            // Dialog Khusus Admin untuk Mengubah PIN Lama Menjadi PIN Baru
            if (showAdminChangePinDialog) {
                AdminChangePinDialog(
                    isDark = isDark,
                    onDismiss = { showAdminChangePinDialog = false },
                    onPinChanged = {
                        showAdminChangePinDialog = false
                    }
                )
            }
        }
        return
    }

    // 2. Active Story Fullscreen Slide Reader Mode ("Fitur Mode Baca")
    if (activeStory != null) {
        val readerStoryList = if (stories.any { it.id == activeStory.id }) stories else listOf(activeStory)
        val initialIndex = readerStoryList.indexOfFirst { it.id == activeStory.id }.coerceAtLeast(0)

        // Mode warna saat pengguna memilih cerita disamakan dengan mode warna saat aplikasi dibuka (Terang vs Gelap)
        val effectiveReaderTheme = remember(preferences.readerTheme, isDark) {
            if (isDark) {
                // Saat aplikasi dibuka dalam mode gelap, mode baca disamakan ke tema gelap
                if (preferences.readerTheme == ReaderTheme.LIGHT || preferences.readerTheme == ReaderTheme.SEPIA) {
                    ReaderTheme.DARK
                } else {
                    preferences.readerTheme
                }
            } else {
                // Saat aplikasi dibuka dalam mode terang, mode baca disamakan ke tema terang
                if (preferences.readerTheme == ReaderTheme.DARK || preferences.readerTheme == ReaderTheme.OLED_BLACK || preferences.readerTheme == ReaderTheme.MIDNIGHT_BLUE) {
                    ReaderTheme.LIGHT
                } else {
                    preferences.readerTheme
                }
            }
        }
        val effectivePreferences = remember(preferences, effectiveReaderTheme) {
            preferences.copy(readerTheme = effectiveReaderTheme)
        }

        key(activeStory.id) {
            ReaderScreen(
                stories = readerStoryList,
                initialStoryIndex = initialIndex,
                preferences = effectivePreferences,
                onBack = { viewModel.closeStoryReader() },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onMarkAsRead = { viewModel.markStoryAsRead(it, true) },
                onUpdateFontSize = { viewModel.updateFontSize(it) },
                onUpdateFontFamily = { viewModel.updateFontFamily(it) },
                onUpdateTheme = { viewModel.updateReaderTheme(it) },
                onUpdateLineHeight = { viewModel.updateLineHeight(it) },
                onSavePreferences = { viewModel.saveReaderPreferences() }
            )
        }
        return
    }

    // 3. Main Application Dashboard & Navigation Hub
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = selectedBottomTab != 1 || isLiveTvControlsVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = if (isDark) Color(0xFF13141F) else Color(0xFFFFFFFF),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        if (isDark) listOf(Color(0x33D4AF37), Color(0x99FFD700), Color(0x33D4AF37))
                        else listOf(Color(0x22B8860B), Color(0x66B8860B), Color(0x22B8860B))
                    )
                ),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_bottom_navigation")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Tombol Cerita (Diperkecil & Mewah)
                    val isCeritaSelected = selectedBottomTab == 0
                    Surface(
                        onClick = {
                            if (!isAgeVerifiedForDashboard) {
                                showPinDialogForDashboard = true
                            } else {
                                selectedBottomTab = 0
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCeritaSelected) {
                            if (isDark) Color(0xFF332912) else Color(0xFFFFF6DF)
                        } else {
                            if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA)
                        },
                        border = if (isCeritaSelected) {
                            BorderStroke(1.2.dp, if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B))
                        } else {
                            BorderStroke(0.6.dp, if (isDark) Color(0xFF2E3040) else Color(0xFFD8DCE6))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("bottom_nav_dashboard")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isCeritaSelected) Icons.Filled.Book else Icons.Outlined.Book,
                                contentDescription = "Cerita",
                                tint = if (isCeritaSelected) {
                                    if (isDark) Color(0xFFFFD700) else Color(0xFF7A5200)
                                } else {
                                    if (isDark) Color.LightGray else Color(0xFF5A5D6B)
                                },
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Cerita",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                fontWeight = if (isCeritaSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isCeritaSelected) {
                                    if (isDark) Color(0xFFFFD700) else Color(0xFF7A5200)
                                } else {
                                    if (isDark) Color.LightGray else Color(0xFF5A5D6B)
                                },
                                maxLines = 1
                            )
                        }
                    }

                    // 2. Tombol Live TV (Diperkecil & Mewah)
                    val isLiveTvSelected = selectedBottomTab == 1
                    Surface(
                        onClick = { selectedBottomTab = 1 },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isLiveTvSelected) {
                            if (isDark) Color(0xFF332912) else Color(0xFFFFF6DF)
                        } else {
                            if (isDark) Color(0xFF1B1C28) else Color(0xFFF3F5FA)
                        },
                        border = if (isLiveTvSelected) {
                            BorderStroke(1.2.dp, if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B))
                        } else {
                            BorderStroke(0.6.dp, if (isDark) Color(0xFF2E3040) else Color(0xFFD8DCE6))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("bottom_nav_livetv")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isLiveTvSelected) Icons.Filled.LiveTv else Icons.Outlined.LiveTv,
                                contentDescription = "Live TV",
                                tint = if (isLiveTvSelected) {
                                    if (isDark) Color(0xFFFFD700) else Color(0xFF7A5200)
                                } else {
                                    if (isDark) Color.LightGray else Color(0xFF5A5D6B)
                                },
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Live TV",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                fontWeight = if (isLiveTvSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isLiveTvSelected) {
                                    if (isDark) Color(0xFFFFD700) else Color(0xFF7A5200)
                                } else {
                                    if (isDark) Color.LightGray else Color(0xFF5A5D6B)
                                },
                                maxLines = 1
                            )
                        }
                    }

                    // 3. Tombol Close / Keluar Aplikasi (Mewah & Berkesan)
                    Surface(
                        onClick = {
                            (context as? Activity)?.finish()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDark) Color(0xFF2B1418) else Color(0xFFFFECEE),
                        border = BorderStroke(1.2.dp, if (isDark) Color(0xFFFFD700).copy(alpha = 0.85f) else Color(0xFFD32F2F)),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("bottom_nav_close_app")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = "Tutup Aplikasi",
                                tint = if (isDark) Color(0xFFFF5252) else Color(0xFFD32F2F),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Keluar",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) Color(0xFFFFD700) else Color(0xFFB71C1C),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    },
    modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedBottomTab) {
                0 -> {
                    androidx.activity.compose.BackHandler {
                        selectedBottomTab = 1
                    }
                    DashboardScreen(
                        stories = stories,
                        searchQuery = searchQuery,
                        syncState = syncState,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onSyncWeb = { viewModel.syncStories() },
                        onOpenStory = { viewModel.openStoryReader(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onOpenModeBacaSettings = { showGlobalReaderSettingsSheet = true },
                        onUpdateGenre = { storyId, newGenre -> viewModel.updateStoryGenre(storyId, newGenre) },
                        onOpenAdminChangePin = { showAdminChangePinDialog = true }
                    )
                }

                else -> LiveStreamingScreen(
                    onCloseToDashboard = {
                        if (!isAgeVerifiedForDashboard) {
                            showPinDialogForDashboard = true
                        } else {
                            selectedBottomTab = 0
                        }
                    },
                    currentThemeMode = appThemeMode,
                    onThemeModeChange = { viewModel.setAppThemeMode(it) },
                    onControlsVisibilityChange = { isLiveTvControlsVisible = it }
                )
            }

            // Dialog Verifikasi PIN Usia Dewasa (17+) sewaktu akan masuk ke dashboard
            if (showPinDialogForDashboard) {
                DashboardPinDialog(
                    isDark = isDark,
                    onDismiss = { showPinDialogForDashboard = false },
                    onPinCorrect = {
                        isAgeVerifiedForDashboard = true
                        showPinDialogForDashboard = false
                        selectedBottomTab = 0
                    },
                    onOpenAdminChangePin = {
                        showAdminChangePinDialog = true
                    }
                )
            }

            // Dialog Khusus Admin untuk Mengubah PIN Lama Menjadi PIN Baru
            if (showAdminChangePinDialog) {
                AdminChangePinDialog(
                    isDark = isDark,
                    onDismiss = { showAdminChangePinDialog = false },
                    onPinChanged = {
                        showAdminChangePinDialog = false
                    }
                )
            }

            // Global Mode Baca Customizer Sheet
            if (showGlobalReaderSettingsSheet) {
                ReaderSettingsSheet(
                    preferences = preferences,
                    onUpdateFontSize = { viewModel.updateFontSize(it) },
                    onUpdateFontFamily = { viewModel.updateFontFamily(it) },
                    onUpdateTheme = { viewModel.updateReaderTheme(it) },
                    onUpdateLineHeight = { viewModel.updateLineHeight(it) },
                    onDismiss = { showGlobalReaderSettingsSheet = false },
                    onSavePreferences = { viewModel.saveReaderPreferences() }
                )
            }
        }
    }
}
