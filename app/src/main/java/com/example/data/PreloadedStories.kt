package com.example.data

object PreloadedStories {
    val GENRES = listOf(
        "Semua",
        "Romantis",
        "Horor",
        "Komedi",
        "Fantasi",
        "Misteri",
        "Petualangan",
        "Kehidupan",
        "Sci-Fi"
    )

    fun getInitialStories(): List<StoryEntity> {
        return emptyList()
    }
}
