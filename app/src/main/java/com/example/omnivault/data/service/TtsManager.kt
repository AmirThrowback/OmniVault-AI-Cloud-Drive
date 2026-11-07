package com.example.omnivault.data.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentActiveFileId = MutableStateFlow<String?>(null)
    val currentActiveFileId: StateFlow<String?> = _currentActiveFileId.asStateFlow()

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                isInitialized = true
                setupProgressListener()
            }
        }
    }

    private fun setupProgressListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _currentActiveFileId.value = null
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _currentActiveFileId.value = null
            }
        })
    }

    fun speak(text: String, fileId: String? = null, speechRate: Float = 1.0f) {
        if (!isInitialized || text.isBlank()) return
        stop()
        _currentActiveFileId.value = fileId
        textToSpeech?.setSpeechRate(speechRate)
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, fileId ?: "omnivault_tts")
        _isSpeaking.value = true
    }

    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
        _currentActiveFileId.value = null
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}
