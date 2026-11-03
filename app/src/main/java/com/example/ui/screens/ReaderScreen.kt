package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReaderFont
import com.example.data.ReaderPreferences
import com.example.data.ReaderTheme
import com.example.data.StoryEntity
import com.example.ui.components.ReaderSettingsSheet
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    stories: List<StoryEntity>,
    initialStoryIndex: Int,
    preferences: ReaderPreferences,
    onBack: () -> Unit,
    onToggleFavorite: (StoryEntity) -> Unit,
    onMarkAsRead: (String) -> Unit = {},
    onUpdateFontSize: (Float) -> Unit,
    onUpdateFontFamily: (ReaderFont) -> Unit,
    onUpdateTheme: (ReaderTheme) -> Unit,
    onUpdateLineHeight: (Float) -> Unit,
    onSavePreferences: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (stories.isEmpty()) {
        onBack()
        return
    }

    val safeInitialIndex = initialStoryIndex.coerceIn(0, stories.size - 1)
    val storyPagerState = rememberPagerState(
        initialPage = safeInitialIndex,
        pageCount = { stories.size }
    )
    val coroutineScope = rememberCoroutineScope()
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showUnreadCollectionSheet by remember { mutableStateOf(false) }

    val currentStory = stories[storyPagerState.currentPage]
    val theme = preferences.readerTheme

    // Automatically mark the current story as read when viewed
    LaunchedEffect(storyPagerState.currentPage) {
        onMarkAsRead(currentStory.id)
    }

    // Helper function to pick and jump to a random next story
    fun goToRandomNextStory() {
        if (stories.size <= 1) return
        val currentIdx = storyPagerState.currentPage
        // Prefer unread stories first
        val unreadCandidates = stories.indices.filter { it != currentIdx && !stories[it].isRead }
        val candidateIndex = if (unreadCandidates.isNotEmpty()) {
            unreadCandidates.random()
        } else {
            val allOtherCandidates = stories.indices.filter { it != currentIdx }
            if (allOtherCandidates.isNotEmpty()) allOtherCandidates.random() else currentIdx
        }
        coroutineScope.launch {
            storyPagerState.animateScrollToPage(candidateIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentStory.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = theme.textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = theme.textColor
                        )
                    }
                },
                actions = {
                    // Random Story Jump Button
                    Surface(
                        color = theme.accentColor,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { goToRandomNextStory() }
                            .testTag("reader_random_story_button")
                            .padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Acak Cerita",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Acak",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Dedicated Koleksi Cerita Button
                    Surface(
                        color = theme.accentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showUnreadCollectionSheet = true }
                            .testTag("reader_koleksi_story_button")
                            .padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Koleksi Cerita",
                                tint = theme.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Koleksi Cerita",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = theme.accentColor
                            )
                        }
                    }

                    // Quick Settings Gear
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("reader_quick_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan Mode Baca",
                            tint = theme.accentColor
                        )
                    }

                    // Close Icon Button (Replaces Heart Button)
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("reader_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup & Kembali ke Dashboard Utama",
                            tint = theme.textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.backgroundColor
                )
            )
        },
        bottomBar = {
            // Reader Control Bar
            Surface(
                color = theme.cardColor,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Progress bar across stories
                    LinearProgressIndicator(
                        progress = { (storyPagerState.currentPage + 1).toFloat() / stories.size.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = theme.accentColor,
                        trackColor = theme.textColor.copy(alpha = 0.1f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prev Story Button
                        IconButton(
                            enabled = storyPagerState.currentPage > 0,
                            onClick = {
                                coroutineScope.launch {
                                    storyPagerState.animateScrollToPage(storyPagerState.currentPage - 1)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Cerita Sebelumnya",
                                tint = if (storyPagerState.currentPage > 0) theme.textColor else theme.textColor.copy(alpha = 0.3f)
                            )
                        }

                        // Quick Font Size Adjuster + Random Story Center Action
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onUpdateFontSize(preferences.fontSizeSp - 2f) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("reader_quick_decrease_font")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Kecilkan Teks",
                                    tint = theme.textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = "${preferences.fontSizeSp.toInt()}sp",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = theme.textColor,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            IconButton(
                                onClick = { onUpdateFontSize(preferences.fontSizeSp + 2f) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("reader_quick_increase_font")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Besarkan Teks",
                                    tint = theme.textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Next Story Button
                        IconButton(
                            enabled = storyPagerState.currentPage < stories.size - 1,
                            onClick = {
                                coroutineScope.launch {
                                    storyPagerState.animateScrollToPage(storyPagerState.currentPage + 1)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Cerita Selanjutnya",
                                tint = if (storyPagerState.currentPage < stories.size - 1) theme.textColor else theme.textColor.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        },
        containerColor = theme.backgroundColor,
        modifier = modifier.testTag("reader_screen_container")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(theme.backgroundColor)
        ) {
            // Main Horizontal Pager for Stories
            HorizontalPager(
                state = storyPagerState,
                modifier = Modifier.fillMaxSize()
            ) { storyIdx ->
                val story = stories[storyIdx]

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    // Measure screen specs dynamically to chunk text into readable slide pages
                    val availableHeightDp = maxHeight.value
                    val availableWidthDp = maxWidth.value
                    val charsPerPage = remember(availableHeightDp, availableWidthDp, preferences.fontSizeSp) {
                        ((availableHeightDp * availableWidthDp) / (preferences.fontSizeSp * 2.2f)).toInt().coerceIn(300, 1800)
                    }

                    val pages = remember(story.content, charsPerPage) {
                        splitStoryIntoPages(story.content, charsPerPage)
                    }

                    val pagePagerState = rememberPagerState(
                        initialPage = 0,
                        pageCount = { pages.size }
                    )

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Inner Slide Page Horizontal Pager
                        HorizontalPager(
                            state = pagePagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { pageIdx ->
                            val pageContent = pages[pageIdx]

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            ) {
                                if (pageIdx == 0) {
                                    Text(
                                        text = story.title,
                                        fontSize = (preferences.fontSizeSp * 1.3f).sp,
                                        fontFamily = preferences.fontFamily.fontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.textColor,
                                        lineHeight = (preferences.fontSizeSp * 1.5f).sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    ) {
                                        Text(
                                            text = "Oleh ${story.author} • ${story.publishedDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = theme.textColor.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                // Page Text Body
                                Text(
                                    text = pageContent,
                                    fontSize = preferences.fontSizeSp.sp,
                                    fontFamily = preferences.fontFamily.fontFamily,
                                    color = theme.textColor,
                                    lineHeight = (preferences.fontSizeSp * preferences.lineHeightMultiplier).sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Last Page Banner & Next Story Options
                                if (pageIdx == pages.size - 1) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = theme.cardColor
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(18.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Tuntas",
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "― CERITA TUNTAS DIBACA ―",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2E7D32)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Cerita #${storyIdx + 1} sudah ditandai tuntas dibaca.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = theme.textColor.copy(alpha = 0.8f)
                                            )

                                            Spacer(modifier = Modifier.height(14.dp))

                                            // 1. Random Next Story Button ("Biar Seru!")
                                            Surface(
                                                color = theme.accentColor,
                                                shape = RoundedCornerShape(14.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable { goToRandomNextStory() }
                                                    .testTag("reader_random_next_story_button")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Shuffle,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Lanjut Cerita Acak (Random) 🎲",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }

                                            if (storyIdx < stories.size - 1) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                // 2. Linear Next Story Button
                                                Surface(
                                                    color = theme.accentColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(14.dp),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(14.dp))
                                                        .clickable {
                                                            coroutineScope.launch {
                                                                storyPagerState.animateScrollToPage(storyIdx + 1)
                                                            }
                                                        }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = "Lanjut Cerita #${storyIdx + 2}",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = theme.accentColor
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                            contentDescription = null,
                                                            tint = theme.accentColor,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // 3. Break / Back to Dashboard
                                            Surface(
                                                color = theme.textColor.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(14.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable { onBack() }
                                                    .testTag("reader_break_end_card_button")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PauseCircle,
                                                        contentDescription = null,
                                                        tint = theme.textColor,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Istirahat Baca (Ke Dashboard)",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = theme.textColor
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Slide Page Indicator Footer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Slide ${pagePagerState.currentPage + 1} dari ${pages.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = theme.textColor.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(pages.size) { dotIdx ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (dotIdx == pagePagerState.currentPage) 8.dp else 6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (dotIdx == pagePagerState.currentPage) theme.accentColor else theme.textColor.copy(alpha = 0.2f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Embedded Reader Settings Bottom Sheet
            if (showSettingsSheet) {
                ReaderSettingsSheet(
                    preferences = preferences,
                    onUpdateFontSize = onUpdateFontSize,
                    onUpdateFontFamily = onUpdateFontFamily,
                    onUpdateTheme = onUpdateTheme,
                    onUpdateLineHeight = onUpdateLineHeight,
                    onDismiss = { showSettingsSheet = false },
                    onSavePreferences = onSavePreferences
                )
            }

            // Embedded Unread Collection Bottom Sheet
            if (showUnreadCollectionSheet) {
                UnreadCollectionSheet(
                    stories = stories,
                    currentStoryId = currentStory.id,
                    theme = theme,
                    onSelectStory = { selectedIdx ->
                        coroutineScope.launch {
                            storyPagerState.animateScrollToPage(selectedIdx)
                        }
                        showUnreadCollectionSheet = false
                    },
                    onDismiss = { showUnreadCollectionSheet = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnreadCollectionSheet(
    stories: List<StoryEntity>,
    currentStoryId: String,
    theme: ReaderTheme,
    onSelectStory: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("unread") } // "unread" or "all"
    var searchQuery by remember { mutableStateOf("") }

    val unreadStories = remember(stories) { stories.filter { !it.isRead } }
    val displayStories = remember(stories, unreadStories, selectedTab, searchQuery) {
        val baseList = if (selectedTab == "unread") unreadStories else stories
        if (searchQuery.isBlank()) baseList
        else baseList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.genre.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
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
                    .heightIn(max = 680.dp)
                    .testTag("unread_collection_dialog"),
                shape = RoundedCornerShape(24.dp),
                color = theme.backgroundColor,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Koleksi Cerita Belum Dibaca",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = theme.textColor
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = theme.textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips (Belum Dibaca vs Semua)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedTab == "unread") theme.accentColor else theme.cardColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedTab = "unread" }
                        .testTag("collection_tab_unread")
                ) {
                    Text(
                        text = "Belum Dibaca (${unreadStories.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == "unread") Color.White else theme.textColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedTab == "all") theme.accentColor else theme.cardColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedTab = "all" }
                        .testTag("collection_tab_all")
                ) {
                    Text(
                        text = "Semua Cerita (${stories.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == "all") Color.White else theme.textColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("collection_search_input"),
                placeholder = { Text("Cari judul cerita...", color = theme.textColor.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = theme.accentColor
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = theme.textColor)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = theme.textColor,
                    unfocusedTextColor = theme.textColor,
                    focusedBorderColor = theme.accentColor,
                    unfocusedBorderColor = theme.textColor.copy(alpha = 0.2f),
                    focusedContainerColor = theme.cardColor,
                    unfocusedContainerColor = theme.cardColor
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (displayStories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == "unread") "Tidak ada cerita yang belum dibaca! 🎉" else "Cerita tidak ditemukan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = theme.textColor.copy(alpha = 0.7f)
                    )
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayStories.size) { idx ->
                        val story = displayStories[idx]
                        val isCurrent = story.id == currentStoryId
                        val originalIndex = stories.indexOfFirst { it.id == story.id }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) theme.accentColor.copy(alpha = 0.15f) else theme.cardColor
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (originalIndex != -1) {
                                        onSelectStory(originalIndex)
                                    }
                                }
                                .testTag("collection_item_${story.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = story.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                        color = theme.textColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${story.genre} • Oleh ${story.author}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = theme.textColor.copy(alpha = 0.6f)
                                    )
                                }

                                if (story.isRead) {
                                    Surface(
                                        color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Tuntas",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        color = theme.accentColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "Belum Dibaca",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = theme.accentColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}
}

private fun splitStoryIntoPages(content: String, charsPerPage: Int): List<String> {
    if (content.isBlank()) return listOf("Konten belum tersedia.")
    if (content.length <= charsPerPage) return listOf(content)

    val pages = mutableListOf<String>()
    val paragraphs = content.split("\n\n")
    var currentChunk = StringBuilder()

    for (paragraph in paragraphs) {
        if (currentChunk.length + paragraph.length > charsPerPage && currentChunk.isNotEmpty()) {
            pages.add(currentChunk.toString().trim())
            currentChunk = StringBuilder()
        }
        if (paragraph.length > charsPerPage) {
            // Split huge paragraph into sentence bounds
            var remaining = paragraph
            while (remaining.length > charsPerPage) {
                val cutIndex = remaining.lastIndexOf(". ", charsPerPage).let { if (it > 0) it + 1 else charsPerPage }
                pages.add(remaining.substring(0, cutIndex).trim())
                remaining = remaining.substring(cutIndex).trim()
            }
            if (remaining.isNotEmpty()) {
                currentChunk.append(remaining).append("\n\n")
            }
        } else {
            currentChunk.append(paragraph).append("\n\n")
        }
    }

    if (currentChunk.isNotBlank()) {
        pages.add(currentChunk.toString().trim())
    }

    return if (pages.isEmpty()) listOf(content) else pages
}
