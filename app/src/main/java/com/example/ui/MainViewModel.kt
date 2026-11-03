@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.IptvChannel
import com.example.data.IptvRepository
import com.example.data.ReaderFont
import com.example.data.ReaderPreferences
import com.example.data.ReaderTheme
import com.example.data.StoryEntity
import com.example.data.StoryRepository
import com.example.data.SyncState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StoryRepository.getInstance(application)
    val iptvRepository: IptvRepository = IptvRepository.getInstance(application)

    // Onboarding / Welcome modal state
    private val _showWelcomeBanner = MutableStateFlow(true)
    val showWelcomeBanner: StateFlow<Boolean> = _showWelcomeBanner.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Admin login state
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // Sync state for stories
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    // Reader Preferences
    private val _readerPreferences = MutableStateFlow(ReaderPreferences.loadFromSharedPreferences(application))
    val readerPreferences: StateFlow<ReaderPreferences> = _readerPreferences.asStateFlow()

    // App Theme Mode (System, Light, Dark)
    private val _appThemeMode = MutableStateFlow(com.example.data.AppThemeMode.loadFromPreferences(application))
    val appThemeMode: StateFlow<com.example.data.AppThemeMode> = _appThemeMode.asStateFlow()

    fun setAppThemeMode(mode: com.example.data.AppThemeMode) {
        _appThemeMode.value = mode
        com.example.data.AppThemeMode.saveToPreferences(getApplication(), mode)
    }

    // Selected story for reading mode
    private val _currentStoryId = MutableStateFlow<String?>(null)
    val currentStoryId: StateFlow<String?> = _currentStoryId.asStateFlow()

    // Stories list dependent on search query
    val stories: StateFlow<List<StoryEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.isNotBlank()) {
            repository.searchStories(query)
        } else {
            repository.allStories
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteStories: StateFlow<List<StoryEntity>> = repository.favoriteStories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val liveTvChannels: StateFlow<List<IptvChannel>> = iptvRepository.channelsFlow
    val isLiveTvUpdating: StateFlow<Boolean> = iptvRepository.isUpdatingFlow

    init {
        // Otomatis memperbarui semua sumber link yang tertera saat aplikasi dibuat (stories & Live TV)
        viewModelScope.launch {
            repository.checkAndPreloadInitialData()
            syncStories(fetchAll = true)
        }
        viewModelScope.launch {
            iptvRepository.autoUpdateAllSources(application)
        }
    }

    fun loginAdmin(user: String, pass: String): Boolean {
        if (com.example.data.SecurityPinManager.verifyAdminCredentials(user, pass)) {
            _isAdminLoggedIn.value = true
            return true
        }
        return false
    }

    fun changeSecurityPin(user: String, pass: String, oldPin: String, newPin: String): Result<Unit> {
        if (!com.example.data.SecurityPinManager.verifyAdminCredentials(user, pass)) {
            return Result.failure(IllegalArgumentException("Username atau Password Admin salah!"))
        }
        if (!com.example.data.SecurityPinManager.verifyPin(getApplication(), oldPin)) {
            return Result.failure(IllegalArgumentException("PIN Lama tidak sesuai!"))
        }
        val cleanNewPin = newPin.trim()
        if (cleanNewPin.length != 4 || !cleanNewPin.all { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("PIN Baru harus terdiri dari 4 digit angka!"))
        }
        val success = com.example.data.SecurityPinManager.setPin(getApplication(), cleanNewPin)
        return if (success) {
            _isAdminLoggedIn.value = true
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Gagal menyimpan PIN baru."))
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun saveReaderPreferences() {
        _readerPreferences.value.saveToSharedPreferences(getApplication())
    }

    fun updateCustomFeedUrl(newUrl: String) {
        val cleanUrl = newUrl.trim()
        if (cleanUrl.isNotBlank()) {
            _readerPreferences.value = _readerPreferences.value.copy(customFeedUrl = cleanUrl)
            saveReaderPreferences()
            syncStories(fetchAll = true)
        }
    }

    fun dismissWelcomeBanner() {
        _showWelcomeBanner.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun syncStories(fetchAll: Boolean = true) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            val result = repository.syncStoriesFromWeb(
                feedUrl = _readerPreferences.value.customFeedUrl,
                fetchAll = fetchAll
            )
            _syncState.value = result
        }
    }

    fun refreshLiveTvSources() {
        viewModelScope.launch {
            iptvRepository.autoUpdateAllSources(getApplication())
        }
    }

    fun toggleFavorite(story: StoryEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(story.id, story.isFavorite)
        }
    }

    fun markStoryAsRead(storyId: String, isRead: Boolean = true) {
        viewModelScope.launch {
            repository.markStoryAsRead(storyId, isRead)
        }
    }

    fun updateStoryGenre(storyId: String, newGenre: String) {
        viewModelScope.launch {
            repository.updateStoryGenre(storyId, newGenre)
        }
    }

    fun openStoryReader(storyId: String) {
        _currentStoryId.value = storyId
        markStoryAsRead(storyId, true)
    }

    fun closeStoryReader() {
        _currentStoryId.value = null
    }

    fun updateFontSize(newSize: Float) {
        val clamped = newSize.coerceIn(12f, 32f)
        _readerPreferences.value = _readerPreferences.value.copy(fontSizeSp = clamped)
        saveReaderPreferences()
    }

    fun updateFontFamily(font: ReaderFont) {
        _readerPreferences.value = _readerPreferences.value.copy(fontFamily = font)
        saveReaderPreferences()
    }

    fun updateReaderTheme(theme: ReaderTheme) {
        _readerPreferences.value = _readerPreferences.value.copy(readerTheme = theme)
        saveReaderPreferences()
    }

    fun updateLineHeight(multiplier: Float) {
        _readerPreferences.value = _readerPreferences.value.copy(lineHeightMultiplier = multiplier)
        saveReaderPreferences()
    }

    fun updateReadingPosition(storyId: String, position: Int) {
        viewModelScope.launch {
            repository.updateReadPosition(storyId, position)
        }
    }
}
