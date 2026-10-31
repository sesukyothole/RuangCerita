package com.example.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

enum class ReaderFont(val label: String, val fontFamily: FontFamily) {
    SERIF("Book Serif", FontFamily.Serif),
    SANS("Clean Sans", FontFamily.SansSerif),
    MONO("Typewriter", FontFamily.Monospace),
    CURSIVE("Cursive", FontFamily.Cursive)
}

enum class ReaderTheme(
    val label: String,
    val backgroundColor: Color,
    val textColor: Color,
    val cardColor: Color,
    val accentColor: Color
) {
    LIGHT(
        label = "Kertas Terang",
        backgroundColor = Color(0xFFFAF8F5),
        textColor = Color(0xFF1E1E1E),
        cardColor = Color(0xFFEFECE6),
        accentColor = Color(0xFFC85A17)
    ),
    SEPIA(
        label = "Sepia Klasik",
        backgroundColor = Color(0xFFFBF0D9),
        textColor = Color(0xFF3E2723),
        cardColor = Color(0xFFF2E2BE),
        accentColor = Color(0xFF8D6E63)
    ),
    DARK(
        label = "Gelap Elegan",
        backgroundColor = Color(0xFF1E1E2C),
        textColor = Color(0xFFE2E8F0),
        cardColor = Color(0xFF28283D),
        accentColor = Color(0xFF818CF8)
    ),
    MIDNIGHT_BLUE(
        label = "Malam Biru",
        backgroundColor = Color(0xFF0F172A),
        textColor = Color(0xFFE0F2FE),
        cardColor = Color(0xFF1E293B),
        accentColor = Color(0xFF38BDF8)
    ),
    OLED_BLACK(
        label = "Hitam OLED",
        backgroundColor = Color(0xFF000000),
        textColor = Color(0xFFF1F5F9),
        cardColor = Color(0xFF121212),
        accentColor = Color(0xFFF43F5E)
    )
}

data class ReaderPreferences(
    val fontSizeSp: Float = 18f,
    val fontFamily: ReaderFont = ReaderFont.SERIF,
    val readerTheme: ReaderTheme = ReaderTheme.SEPIA,
    val lineHeightMultiplier: Float = 1.5f,
    val showReadingProgress: Boolean = true,
    val customFeedUrl: String = "https://iseng-isex.blogspot.com/feeds/posts/default?alt=json"
) {
    fun saveToSharedPreferences(context: Context) {
        val sp = context.getSharedPreferences("reader_settings_prefs", Context.MODE_PRIVATE)
        sp.edit()
            .putFloat("font_size", fontSizeSp)
            .putString("font_family", fontFamily.name)
            .putString("reader_theme", readerTheme.name)
            .putFloat("line_height", lineHeightMultiplier)
            .putBoolean("show_progress", showReadingProgress)
            .putString("feed_url", customFeedUrl)
            .apply()
    }

    companion object {
        fun loadFromSharedPreferences(context: Context): ReaderPreferences {
            val sp = context.getSharedPreferences("reader_settings_prefs", Context.MODE_PRIVATE)
            val fontSize = sp.getFloat("font_size", 18f)
            val fontName = sp.getString("font_family", ReaderFont.SERIF.name) ?: ReaderFont.SERIF.name
            val themeName = sp.getString("reader_theme", ReaderTheme.SEPIA.name) ?: ReaderTheme.SEPIA.name
            val lineHeight = sp.getFloat("line_height", 1.5f)
            val showProgress = sp.getBoolean("show_progress", true)
            val feedUrl = sp.getString("feed_url", "https://iseng-isex.blogspot.com/feeds/posts/default?alt=json") ?: "https://iseng-isex.blogspot.com/feeds/posts/default?alt=json"

            val font = try { ReaderFont.valueOf(fontName) } catch (e: Exception) { ReaderFont.SERIF }
            val theme = try { ReaderTheme.valueOf(themeName) } catch (e: Exception) { ReaderTheme.SEPIA }

            return ReaderPreferences(
                fontSizeSp = fontSize,
                fontFamily = font,
                readerTheme = theme,
                lineHeightMultiplier = lineHeight,
                showReadingProgress = showProgress,
                customFeedUrl = feedUrl
            )
        }
    }
}

