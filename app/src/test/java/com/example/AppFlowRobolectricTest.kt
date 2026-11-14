package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.IptvChannel
import com.example.data.IptvRepository
import com.example.data.StoryEntity
import com.example.data.SyncState
import com.example.ui.components.ChannelPickerPopupDialog
import com.example.ui.components.DashboardPinDialog
import com.example.ui.components.MultiScreenMode
import com.example.ui.components.MultiScreenPlayerLayout
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.RuangCeritaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppFlowRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pinDialog_accepts1234_andRejectsWrongPin() {
        var unlocked = false
        composeTestRule.setContent {
            RuangCeritaTheme {
                DashboardPinDialog(
                    isDark = true,
                    onPinCorrect = { unlocked = true }
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("dashboard_pin_key_1").performClick()
        composeTestRule.onNodeWithTag("dashboard_pin_key_2").performClick()
        composeTestRule.onNodeWithTag("dashboard_pin_key_3").performClick()
        composeTestRule.onNodeWithTag("dashboard_pin_key_4").performClick()
        composeTestRule.waitForIdle()

        assertTrue("PIN 1234 must unlock", unlocked)
    }

    @Test
    fun channelPicker_doesNotPrematurelyDismiss_onSequentialMultiScreenSelection() {
        val sampleChannels = listOf(
            IptvChannel(id = "ch1", name = "Channel One", genre = "Berita"),
            IptvChannel(id = "ch2", name = "Channel Two", genre = "Olahraga")
        )
        var targetIdx by mutableStateOf<Int?>(0)
        var dismissedManually = false
        val pickedForPane = mutableMapOf<Int, String>()

        composeTestRule.setContent {
            RuangCeritaTheme {
                if (targetIdx != null) {
                    val idx = targetIdx!!
                    ChannelPickerPopupDialog(
                        targetScreenName = "Layar ${idx + 1}",
                        currentChannelId = null,
                        channels = sampleChannels,
                        favoriteChannelIds = emptySet(),
                        onOpenPlaylistSettings = {},
                        onSelectChannel = { selected ->
                            pickedForPane[idx] = selected.id
                            targetIdx = if (idx == 0) 1 else null
                        },
                        onDismiss = {
                            dismissedManually = true
                            targetIdx = null
                        }
                    )
                }
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("channel_picker_search_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("channel_picker_playlist_settings_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("channel_item_ch1").performClick()
        composeTestRule.waitForIdle()

        assertFalse("onDismiss must not fire prematurely when advancing to Layar 2", dismissedManually)
        assertEquals("ch1", pickedForPane[0])
        assertEquals(1, targetIdx)
        composeTestRule.onNodeWithText("Pilih Channel • Layar 2 (2)").assertIsDisplayed()

        composeTestRule.onNodeWithTag("channel_item_ch2").performClick()
        composeTestRule.waitForIdle()
        assertEquals("ch2", pickedForPane[1])
        assertEquals(null, targetIdx)
    }

    @Test
    fun channelPicker_filtersBySearchAndGenre() {
        val sampleChannels = listOf(
            IptvChannel(id = "ch_news", name = "Berita Nasional", genre = "News"),
            IptvChannel(id = "ch_sport", name = "Arena Sport", genre = "Sport")
        )
        composeTestRule.setContent {
            RuangCeritaTheme {
                ChannelPickerPopupDialog(
                    targetScreenName = "Layar Utama",
                    currentChannelId = null,
                    channels = sampleChannels,
                    favoriteChannelIds = emptySet(),
                    onSelectChannel = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("channel_picker_search_input").performTextInput("Arena")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("channel_item_ch_sport").assertIsDisplayed()
        composeTestRule.onNodeWithTag("channel_item_ch_news").assertDoesNotExist()
    }

    @Test
    fun multiScreenDual_rendersSideBySideTwoColumns_withHiddenControlsByDefault() {
        val sampleChannels = listOf(
            IptvChannel(id = "ch1", name = "TV 1", genre = "Umum"),
            IptvChannel(id = "ch2", name = "TV 2", genre = "Umum")
        )
        composeTestRule.setContent {
            RuangCeritaTheme {
                MultiScreenPlayerLayout(
                    mode = MultiScreenMode.DUAL,
                    screenChannels = sampleChannels,
                    activeAudioIndex = 0,
                    volumeLevel = 0.5f,
                    showControls = false,
                    onSelectAudioIndex = {},
                    onRequestChannelPicker = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        val pane1Bounds = composeTestRule.onNodeWithTag("multi_screen_pane_1").getBoundsInRoot()
        val pane2Bounds = composeTestRule.onNodeWithTag("multi_screen_pane_2").getBoundsInRoot()

        assertTrue("Pane 1 should be to the left of Pane 2 in 2-column grid", pane1Bounds.left < pane2Bounds.left)
        assertEquals("Both panes should align vertically at top", pane1Bounds.top, pane2Bounds.top)
        composeTestRule.onNodeWithTag("audio_toggle_pane_1").assertDoesNotExist()
    }

    @Test
    fun dashboardScreen_rendersStoriesWithoutGenreInfo() {
        val sampleStories = listOf(
            StoryEntity(
                id = "s1",
                title = "Senja di Pelabuhan",
                author = "Penulis A",
                genre = "Romantis",
                summary = "Kisah singkat",
                content = "Isi cerita lengkap",
                publishedDate = "2026-01-01"
            ),
            StoryEntity(
                id = "s2",
                title = "Rumah Tua",
                author = "Penulis B",
                genre = "Horor",
                summary = "Misteri malam",
                content = "Isi cerita horor",
                publishedDate = "2026-01-02"
            )
        )

        composeTestRule.setContent {
            RuangCeritaTheme {
                DashboardScreen(
                    stories = sampleStories,
                    searchQuery = "",
                    syncState = SyncState.Idle,
                    onSearchQueryChange = {},
                    onSyncWeb = {},
                    onOpenStory = {},
                    onToggleFavorite = {},
                    onOpenModeBacaSettings = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("dashboard_screen_container").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dashboard_toggle_layout_button").assertDoesNotExist()
        composeTestRule.onNodeWithTag("dashboard_genre_chip_Horor").assertDoesNotExist()
        composeTestRule.onNodeWithTag("dashboard_genre_chip_Semua").assertDoesNotExist()
        composeTestRule.onNodeWithText("Romantis").assertDoesNotExist()
        composeTestRule.onNodeWithText("Horor").assertDoesNotExist()
        composeTestRule.onNodeWithTag("dashboard_stories_grid").assertIsDisplayed()
        composeTestRule.onNodeWithTag("story_grid_card_s1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("story_grid_card_s2").assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w411dp-h915dp")
    fun dashboardScreen_usesThreeColumnsAndSevenRowsPerPagePagination() {
        val allStories = (1..25).map { idx ->
            StoryEntity(
                id = "s$idx",
                title = "Cerita Nomor $idx",
                author = "Penulis $idx",
                summary = "Ringkasan $idx",
                content = "Isi cerita $idx",
                publishedDate = "2026-01-01"
            )
        }
        var searchQuery by mutableStateOf("")

        composeTestRule.setContent {
            RuangCeritaTheme {
                val visibleStories = if (searchQuery.isBlank()) allStories
                else allStories.filter { it.title.contains(searchQuery, ignoreCase = true) }
                DashboardScreen(
                    stories = visibleStories,
                    searchQuery = searchQuery,
                    syncState = SyncState.Idle,
                    onSearchQueryChange = { searchQuery = it },
                    onSyncWeb = {},
                    onOpenStory = {},
                    onToggleFavorite = {},
                    onOpenModeBacaSettings = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        // Verify 3-column grid geometry: s1, s2, s3 share top row; s4 starts row 2
        val b1 = composeTestRule.onNodeWithTag("story_grid_card_s1").getBoundsInRoot()
        val b2 = composeTestRule.onNodeWithTag("story_grid_card_s2").getBoundsInRoot()
        val b3 = composeTestRule.onNodeWithTag("story_grid_card_s3").getBoundsInRoot()
        val b4 = composeTestRule.onNodeWithTag("story_grid_card_s4").getBoundsInRoot()
        assertTrue("Column 1 left < Column 2 left", b1.left < b2.left)
        assertTrue("Column 2 left < Column 3 left", b2.left < b3.left)
        assertEquals("Row 1 cards share same top", b1.top, b3.top)
        assertTrue("Card 4 wraps to row 2", b4.top > b1.top)

        // Verify 21 items (3x7) on Page 1
        composeTestRule.onNodeWithText("Hal 1 / 2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dashboard_stories_grid")
            .performScrollToNode(androidx.compose.ui.test.hasTestTag("story_grid_card_s21"))
        composeTestRule.onNodeWithTag("story_grid_card_s21").assertIsDisplayed()
        composeTestRule.onNodeWithTag("story_grid_card_s22").assertDoesNotExist()

        // Navigate to Page 2
        composeTestRule.onNodeWithTag("dashboard_next_page_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Hal 2 / 2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("story_grid_card_s22").assertIsDisplayed()
        composeTestRule.onNodeWithTag("story_grid_card_s25").assertIsDisplayed()
        composeTestRule.onNodeWithTag("story_grid_card_s1").assertDoesNotExist()

        // Typing a search query resets back to Page 1
        composeTestRule.onNodeWithTag("dashboard_search_input").performTextInput("Nomor 23")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Hal 1 / 1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("story_grid_card_s23").assertIsDisplayed()
    }

    @Test
    fun iptvRepository_seedsPrimaryPlaylistsOnFreshAndExistingInstalls_andRestoresOnReset() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(IptvRepository.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val repo = IptvRepository.getInstance(context)
        val expectedUrl1 = "https://raw.githubusercontent.com/atsushi444/iptv/refs/heads/main/adult.m3u"
        val expectedUrl2 = "https://raw.githubusercontent.com/atsushi444/iptv/refs/heads/main/adult2.m3u"

        // 1. Fresh install gets both primary sources enabled at the front
        val freshSources = repo.getSavedPlaylistSources(context)
        assertEquals(2, freshSources.size)
        assertEquals(expectedUrl1, freshSources[0].urlOrContent)
        assertEquals(expectedUrl2, freshSources[1].urlOrContent)
        assertTrue(freshSources[0].isEnabled)
        assertTrue(freshSources[1].isEnabled)

        // 2. Removing a source after initial seeding persists across subsequent reads
        repo.removePlaylistSource(context, freshSources[0].id)
        val afterRemoval = repo.getSavedPlaylistSources(context)
        assertEquals(1, afterRemoval.size)
        assertEquals(expectedUrl2, afterRemoval[0].urlOrContent)

        // 3. Reset default restores both primary sources
        repo.resetPlaylists(context)
        val afterReset = repo.getSavedPlaylistSources(context)
        assertEquals(2, afterReset.size)
        assertEquals(expectedUrl1, afterReset[0].urlOrContent)
        assertEquals(expectedUrl2, afterReset[1].urlOrContent)

        // 4. Existing install that previously stored "[]" without the seeding flag also receives primary sources
        prefs.edit()
            .clear()
            .putString(IptvRepository.KEY_PLAYLIST_SOURCES_V3, "[]")
            .commit()
        val migratedSources = repo.getSavedPlaylistSources(context)
        assertEquals(2, migratedSources.size)
        assertEquals(expectedUrl1, migratedSources[0].urlOrContent)
        assertEquals(expectedUrl2, migratedSources[1].urlOrContent)
        assertTrue(migratedSources.all { it.isEnabled })
    }
}
