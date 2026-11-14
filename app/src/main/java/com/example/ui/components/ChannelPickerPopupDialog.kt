package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.ChannelSortMode
import com.example.data.IptvChannel

@Composable
fun ChannelPickerPopupDialog(
    targetScreenName: String,
    currentChannelId: String?,
    channels: List<IptvChannel>,
    favoriteChannelIds: Set<String>,
    initialSortMode: ChannelSortMode = ChannelSortMode.ORIGINAL,
    onSortModeChanged: ((ChannelSortMode) -> Unit)? = null,
    onOpenPlaylistSettings: (() -> Unit)? = null,
    onSelectChannel: (IptvChannel) -> Unit,
    onDismiss: () -> Unit,
    isDark: Boolean = true
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Semua Channel") }
    var currentSort by remember(initialSortMode) { mutableStateOf(initialSortMode) }

    val goldAccent = if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
    val goldSoft = if (isDark) Color(0x33D4AF37) else Color(0x22B8860B)
    val bgColor = if (isDark) Color(0xFF13141F) else Color(0xFFFFFFFF)
    val cardBg = if (isDark) Color(0xFF1E202E) else Color(0xFFF3F5FA)
    val textColor = if (isDark) Color.White else Color(0xFF181920)
    val textSecondary = if (isDark) Color.LightGray else Color(0xFF555866)

    // Build unique genres list dengan "Semua Channel" sebagai posisi tetap pertama
    val genres = remember(channels, favoriteChannelIds) {
        val list = mutableListOf("Semua Channel", "⭐ Favorit")
        val distinctGenres = channels.map { it.genre.ifBlank { "Lainnya" } }
            .filter { it != "Semua Channel" && it != "Semua" && it != "⭐ Favorit" }
            .distinct()
            .sorted()
        list.addAll(distinctGenres)
        list
    }

    // Sort channels based on currentSort (ORIGINAL playlist, ASCENDING A-Z, DESCENDING Z-A)
    val sortedChannels = remember(channels, currentSort) {
        when (currentSort) {
            ChannelSortMode.ORIGINAL -> channels
            ChannelSortMode.ASCENDING -> channels.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            ChannelSortMode.DESCENDING -> channels.sortedWith(compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name })
        }
    }

    // Filter channels based on search and genre
    val filteredChannels = remember(sortedChannels, searchQuery, selectedGenre, favoriteChannelIds) {
        sortedChannels.filter { channel ->
            val matchGenre = when (selectedGenre) {
                "Semua Channel", "Semua" -> true
                "⭐ Favorit" -> favoriteChannelIds.contains(channel.id)
                else -> channel.genre.equals(selectedGenre, ignoreCase = true)
            }
            val matchSearch = searchQuery.isBlank() ||
                channel.name.contains(searchQuery, ignoreCase = true) ||
                channel.genre.contains(searchQuery, ignoreCase = true)
            matchGenre && matchSearch
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp
    val screenWidthDp = configuration.screenWidthDp

    val maxDialogHeight = (screenHeightDp * 0.86f).dp
    val dialogWidth = (screenWidthDp * 0.84f).dp

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                border = BorderStroke(
                    1.3.dp,
                    Brush.horizontalGradient(listOf(goldSoft, goldAccent, goldSoft))
                ),
                tonalElevation = 10.dp,
                shadowElevation = 16.dp,
                modifier = Modifier
                    .widthIn(max = dialogWidth)
                    .fillMaxWidth(0.84f)
                    .heightIn(min = 160.dp, max = maxDialogHeight)
                    .fillMaxHeight(0.86f)
                    .testTag("channel_picker_popup_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    // Compact Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = goldAccent.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, goldAccent),
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LiveTv,
                                        contentDescription = null,
                                        tint = goldAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pilih Channel • $targetScreenName (${filteredChannels.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                                fontWeight = FontWeight.Bold,
                                color = goldAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("close_channel_picker_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Single Compact Row: Kotak Pencarian Diperkecil + Tombol Pengaturan Icon-Only (Tanda Gambar)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Compact Search Box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = cardBg,
                            border = BorderStroke(0.7.dp, goldSoft),
                            modifier = Modifier
                                .weight(1f)
                                .height(31.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Cari",
                                    tint = goldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Cari channel...",
                                            fontSize = 11.sp,
                                            color = textSecondary.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                        textStyle = TextStyle(
                                            color = textColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        cursorBrush = SolidColor(goldAccent),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("channel_picker_search_input")
                                    )
                                }
                                if (searchQuery.isNotBlank()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus Pencarian",
                                        tint = textSecondary,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { searchQuery = "" }
                                    )
                                }
                            }
                        }

                        // Simple Icon-Only Sort & Settings Buttons in the same row
                        val sortOptions = listOf(
                            Triple(ChannelSortMode.ORIGINAL, Icons.Default.FormatListNumbered, "Urutan Asli Playlist"),
                            Triple(ChannelSortMode.ASCENDING, Icons.Default.ArrowUpward, "Urutkan A - Z"),
                            Triple(ChannelSortMode.DESCENDING, Icons.Default.ArrowDownward, "Urutkan Z - A")
                        )

                        sortOptions.forEach { (mode, icon, contentDesc) ->
                            val isSelected = currentSort == mode
                            Surface(
                                onClick = {
                                    currentSort = mode
                                    onSortModeChanged?.invoke(mode)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) goldAccent.copy(alpha = 0.24f) else cardBg,
                                border = BorderStroke(
                                    if (isSelected) 1.1.dp else 0.7.dp,
                                    if (isSelected) goldAccent else goldSoft.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .size(31.dp)
                                    .testTag(
                                        when (mode) {
                                            ChannelSortMode.ORIGINAL -> "channel_picker_sort_original"
                                            ChannelSortMode.ASCENDING -> "channel_picker_sort_asc"
                                            ChannelSortMode.DESCENDING -> "channel_picker_sort_desc"
                                        }
                                    )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = contentDesc,
                                        tint = if (isSelected) goldAccent else textSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        if (onOpenPlaylistSettings != null) {
                            Surface(
                                onClick = onOpenPlaylistSettings,
                                shape = RoundedCornerShape(8.dp),
                                color = cardBg,
                                border = BorderStroke(0.7.dp, goldSoft.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .size(31.dp)
                                    .testTag("channel_picker_playlist_settings_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Pengaturan Playlist",
                                        tint = goldAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Simple Genre List (Compact Pills)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(genres) { genre ->
                            val isSelected = selectedGenre == genre
                            Surface(
                                onClick = { selectedGenre = genre },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) goldAccent.copy(alpha = 0.22f) else cardBg,
                                border = BorderStroke(
                                    if (isSelected) 0.9.dp else 0.5.dp,
                                    if (isSelected) goldAccent else goldSoft.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = genre,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) goldAccent else textSecondary,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Compact Channel Grid (Kotak lebih kecil agar isi list lebih banyak)
                    if (filteredChannels.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = textSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (selectedGenre == "⭐ Favorit") "Belum ada channel favorit.\nKetuk ikon ❤️ pada channel untuk menambahkannya!"
                                    else "Tidak ada channel ditemukan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 94.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .testTag("channel_picker_grid"),
                            contentPadding = PaddingValues(vertical = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(filteredChannels, key = { it.id }) { channel ->
                                val isSelectedForThisScreen = channel.id == currentChannelId
                                Card(
                                    shape = RoundedCornerShape(7.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelectedForThisScreen)
                                            goldAccent.copy(alpha = 0.18f)
                                        else
                                            cardBg
                                    ),
                                    border = BorderStroke(
                                        if (isSelectedForThisScreen) 1.2.dp else 0.5.dp,
                                        if (isSelectedForThisScreen) goldAccent else goldSoft.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // Biarkan parent (LiveStreamingScreen) mengelola penutupan atau lanjut ke layar berikutnya
                                            onSelectChannel(channel)
                                        }
                                        .testTag("channel_item_${channel.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Smaller Logo
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isDark) Color(0xFF14151F) else Color(0xFFE5E7EB)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (channel.logoUrl.isNotBlank()) {
                                                AsyncImage(
                                                    model = channel.logoUrl,
                                                    contentDescription = channel.name,
                                                    modifier = Modifier.size(18.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Tv,
                                                    contentDescription = null,
                                                    tint = goldAccent,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(5.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = channel.name,
                                                fontSize = 10.sp,
                                                lineHeight = 11.5.sp,
                                                fontWeight = if (isSelectedForThisScreen) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelectedForThisScreen) goldAccent else textColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = channel.genre.ifBlank { "TV" },
                                                fontSize = 8.sp,
                                                lineHeight = 9.5.sp,
                                                color = textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        if (isSelectedForThisScreen) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Sedang Aktif",
                                                tint = goldAccent,
                                                modifier = Modifier.size(11.dp)
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
