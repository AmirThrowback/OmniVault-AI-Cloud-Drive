package com.example.omnivault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omnivault.data.model.FileCategory
import com.example.omnivault.data.model.StorageStats
import com.example.omnivault.data.model.VaultFile
import com.example.omnivault.data.repository.VaultRepository
import com.example.omnivault.data.service.GeminiApiService
import com.example.omnivault.data.service.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultViewModel(
    private val repository: VaultRepository,
    private val geminiApiService: GeminiApiService,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FileCategory.ALL)
    val selectedCategory: StateFlow<FileCategory> = _selectedCategory.asStateFlow()

    private val _selectedFile = MutableStateFlow<VaultFile?>(null)
    val selectedFile: StateFlow<VaultFile?> = _selectedFile.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _showLockDialog = MutableStateFlow(false)
    val showLockDialog: StateFlow<Boolean> = _showLockDialog.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    val isVaultUnlocked: StateFlow<Boolean> = repository.isVaultUnlocked
    val isTtsSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val activeTtsFileId: StateFlow<String?> = ttsManager.currentActiveFileId

    val allFiles: StateFlow<List<VaultFile>> = repository.files

    val filteredFiles: StateFlow<List<VaultFile>> = combine(
        repository.files,
        _searchQuery,
        _selectedCategory,
        repository.isVaultUnlocked
    ) { files, query, category, unlocked ->
        files.filter { file ->
            val matchesCategory = when (category) {
                FileCategory.ALL -> true
                FileCategory.DOCUMENTS -> file.category == FileCategory.DOCUMENTS
                FileCategory.IMAGES -> file.category == FileCategory.IMAGES
                FileCategory.NOTES -> file.category == FileCategory.NOTES
                FileCategory.VAULT -> file.isEncrypted
            }

            val q = query.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                    file.name.lowercase().contains(q) ||
                    file.tags.any { it.lowercase().contains(q) } ||
                    (file.ocrText?.lowercase()?.contains(q) == true) ||
                    file.content.lowercase().contains(q)

            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val storageStats: StateFlow<StorageStats> = combine(
        repository.files,
        repository.isVaultUnlocked
    ) { _, _ ->
        repository.calculateStats()
    }.stateIn(viewModelScope, SharingStarted.Lazily, repository.calculateStats())

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: FileCategory) {
        _selectedCategory.value = category
    }

    fun openFileDetail(file: VaultFile) {
        if (file.isEncrypted && !isVaultUnlocked.value) {
            _showLockDialog.value = true
        } else {
            _selectedFile.value = file
        }
    }

    fun closeFileDetail() {
        _selectedFile.value = null
        ttsManager.stop()
    }

    fun openAddFileDialog() {
        _showAddDialog.value = true
    }

    fun closeAddFileDialog() {
        _showAddDialog.value = false
    }

    fun openLockDialog() {
        _pinError.value = null
        _showLockDialog.value = true
    }

    fun closeLockDialog() {
        _showLockDialog.value = false
        _pinError.value = null
    }

    fun submitPin(pin: String): Boolean {
        val success = repository.verifyPin(pin)
        if (success) {
            _showLockDialog.value = false
            _pinError.value = null
        } else {
            _pinError.value = "Incorrect PIN. Default demo PIN is 1234."
        }
        return success
    }

    fun lockVault() {
        repository.lockVault()
        if (_selectedFile.value?.isEncrypted == true) {
            closeFileDetail()
        }
    }

    fun addFile(
        name: String,
        content: String,
        category: FileCategory,
        mimeType: String,
        isEncrypted: Boolean,
        tags: List<String>
    ) {
        val file = repository.addFile(
            name = name,
            content = content,
            category = category,
            mimeType = mimeType,
            isEncrypted = isEncrypted,
            tags = tags
        )
        _showAddDialog.value = false
        // Automatically analyze new file
        analyzeFileWithAi(file)
    }

    fun deleteFile(fileId: String) {
        if (_selectedFile.value?.id == fileId) {
            closeFileDetail()
        }
        repository.deleteFile(fileId)
    }

    fun analyzeFileWithAi(file: VaultFile) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val updated = geminiApiService.analyzeDocument(file)
                repository.updateFile(updated)
                if (_selectedFile.value?.id == file.id) {
                    _selectedFile.value = updated
                }
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun playTtsNarration(file: VaultFile) {
        val textToSpeak = buildString {
            append("File: ${file.name}. ")
            if (!file.analysisSummary.isNullOrBlank()) {
                append("Summary: ${file.analysisSummary}. ")
            }
            if (!file.ocrText.isNullOrBlank() && file.ocrText != file.content) {
                append("OCR Text: ${file.ocrText}. ")
            }
            if (file.content.isNotBlank() && file.content.length < 1000) {
                append("Content: ${file.content}. ")
            }
        }
        ttsManager.speak(text = textToSpeak, fileId = file.id)
    }

    fun stopTtsNarration() {
        ttsManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
