package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StoryEntity
import com.example.data.SyncState
import com.example.ui.components.EditGenreDialog
import com.example.ui.components.StoryCard
import com.example.ui.components.StoryGridCard

@Composable
fun DashboardScreen(
    stories: List<StoryEntity>,
    searchQuery: String,
    syncState: SyncState,
    onSearchQueryChange: (String) -> Unit,
    onSyncWeb: () -> Unit,
    onOpenStory: (String) -> Unit,
    onToggleFavorite: (StoryEntity) -> Unit,
    onOpenModeBacaSettings: () -> Unit,
    onUpdateGenre: (String, String) -> Unit = { _, _ -> },
    onOpenAdminChangePin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedGenre by remember { mutableStateOf("Semua") }
    var isGridView by remember { mutableStateOf(false) }
    var editingGenreStory by remember { mutableStateOf<StoryEntity?>(null) }
    var showBookmarksOverlay by remember { mutableStateOf(false) }
    var readStatusFilterOverlay by remember { mutableStateOf<ReadStatusFilter?>(null) }

    val favoriteStories = remember(stories) { stories.filter { it.isFavorite } }
    val completedCount = remember(stories) { stories.count { it.isRead } }
    val unreadCount = remember(stories) { stories.count { !it.isRead } }

    val allDistinctGenres = remember(stories) {
        stories.map { it.genre.ifBlank { "Umum" } }
            .distinct()
            .sorted()
    }

    val genreTabs = remember(allDistinctGenres) {
        listOf("Semua", "⭐ Favorit") + allDistinctGenres.filter { it != "Semua" && it != "⭐ Favorit" }
    }

    val filteredStories = remember(stories, selectedGenre) {
        when (selectedGenre) {
            "Semua" -> stories
            "⭐ Favorit" -> stories.filter { it.isFavorite }
            else -> stories.filter { it.genre.equals(selectedGenre, ignoreCase = true) }
        }
    }

    // Sub-screen overlays with proper BackHandler
    if (readStatusFilterOverlay != null) {
        BackHandler { readStatusFilterOverlay = null }
        ReadStatusScreen(
            stories = stories,
            initialFilter = readStatusFilterOverlay ?: ReadStatusFilter.ALL,
            onOpenStory = onOpenStory,
            onToggleFavorite = onToggleFavorite,
            onClose = { readStatusFilterOverlay = null }
        )
        return
    }

    if (showBookmarksOverlay) {
        BackHandler { showBookmarksOverlay = false }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Koleksi Favorit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { showBookmarksOverlay = false },
                    modifier = Modifier.testTag("close_bookmarks_overlay")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup Favorit"
                    )
                }
            }
            BookmarksScreen(
                favoriteStories = favoriteStories,
                onOpenStory = onOpenStory,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.weight(1f)
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 14.dp)
            .testTag("dashboard_screen_container")
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ruang Cerita",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${stories.size} cerita tersedia • Mode Baca Portait",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onSyncWeb,
                    enabled = syncState !is SyncState.Syncing,
                    modifier = Modifier.testTag("dashboard_sync_button")
                ) {
                    if (syncState is SyncState.Syncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sinkronisasi Cerita",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = { isGridView = !isGridView },
                    modifier = Modifier.testTag("dashboard_toggle_layout_button")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                        contentDescription = if (isGridView) "Tampilan Daftar" else "Tampilan Grid",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onOpenModeBacaSettings,
                    modifier = Modifier.testTag("dashboard_reader_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Pengaturan Mode Baca",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onOpenAdminChangePin,
                    modifier = Modifier.testTag("dashboard_admin_pin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Ubah PIN",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sync status feedback pill
        AnimatedVisibility(
            visible = syncState is SyncState.Success || syncState is SyncState.Error,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val isError = syncState is SyncState.Error
            val msg = when (syncState) {
                is SyncState.Success -> syncState.message
                is SyncState.Error -> syncState.message
                else -> ""
            }
            if (msg.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Cari judul, penulis, atau genre cerita...",
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari Cerita",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus Pencarian"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Stats Strip (Favorit / Tuntas / Belum Dibaca)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = { showBookmarksOverlay = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .weight(1f)
                    .testTag("dashboard_stat_favorites")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${favoriteStories.size} Favorit",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Surface(
                onClick = { readStatusFilterOverlay = ReadStatusFilter.COMPLETED },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .weight(1f)
                    .testTag("dashboard_stat_completed")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$completedCount Tuntas",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Surface(
                onClick = { readStatusFilterOverlay = ReadStatusFilter.UNREAD },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .weight(1f)
                    .testTag("dashboard_stat_unread")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PendingActions,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$unreadCount Belum",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Genre Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(genreTabs) { genre ->
                val isSelected = selectedGenre == genre
                Surface(
                    onClick = { selectedGenre = genre },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = if (isSelected) null else BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.testTag("dashboard_genre_chip_$genre")
                ) {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stories Content
        if (filteredStories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Belum ada cerita yang cocok",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Coba ubah kata kunci pencarian, pilih genre lain, atau ketuk ikon Sinkronisasi di kanan atas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("dashboard_stories_grid"),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(filteredStories, key = { _, story -> story.id }) { index, story ->
                    StoryGridCard(
                        story = story,
                        storyNumber = index + 1,
                        onClick = { onOpenStory(story.id) },
                        onToggleFavorite = { onToggleFavorite(story) },
                        onEditGenre = { editingGenreStory = story }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("dashboard_stories_list"),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(filteredStories, key = { _, story -> story.id }) { index, story ->
                    StoryCard(
                        story = story,
                        storyNumber = index + 1,
                        onClick = { onOpenStory(story.id) },
                        onToggleFavorite = { onToggleFavorite(story) },
                        onEditGenre = { editingGenreStory = story }
                    )
                }
            }
        }
    }

    editingGenreStory?.let { targetStory ->
        EditGenreDialog(
            story = targetStory,
            allExistingGenres = allDistinctGenres,
            onSaveGenre = { newGenre ->
                onUpdateGenre(targetStory.id, newGenre)
            },
            onDismiss = { editingGenreStory = null }
        )
    }
}
