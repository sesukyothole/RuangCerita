package com.example.data

data class IptvChannel(
    val id: String,
    val name: String,
    val genre: String,
    val logoUrl: String = "",
    val streamUrl: String = "",
    val licenseType: String = "",
    val licenseKey: String = "",
    val userAgent: String = "",
    val referer: String = "",
    val isMpd: Boolean = false
)

enum class PlaylistSourceType {
    URL,
    LOCAL_FILE,
    CUSTOM_TEXT
}

data class PlaylistSource(
    val id: String,
    val name: String,
    val urlOrContent: String,
    val type: PlaylistSourceType,
    val isEnabled: Boolean = true
)

enum class ChannelSortMode(val title: String, val subtitle: String) {
    ORIGINAL("Urutan Asli", "Sesuai nomor & urutan playlist"),
    ASCENDING("A - Z (Naik)", "Nama diurutkan dari A ke Z"),
    DESCENDING("Z - A (Turun)", "Nama diurutkan dari Z ke A")
}
