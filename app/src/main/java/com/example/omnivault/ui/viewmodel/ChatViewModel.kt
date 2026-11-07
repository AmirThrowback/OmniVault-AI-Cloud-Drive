package com.example.omnivault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omnivault.data.model.ChatMessage
import com.example.omnivault.data.model.VaultFile
import com.example.omnivault.data.service.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val geminiApiService: GeminiApiService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(getInitialMessages())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedModel = MutableStateFlow("gemini-3.8-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    val quickPrompts = listOf(
        "Gemini, मुझे आईडिया दो ऑनलाइन ड्राइव कैसे बनाएं?",
        "Compare Cloudflare R2 vs AWS S3 egress costs",
        "How does client-side zero-knowledge encryption work?",
        "Analyze my current vault inventory and security"
    )

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    fun sendMessage(userText: String, driveContext: List<VaultFile>) {
        if (userText.isBlank() || _isLoading.value) return

        val userMsg = ChatMessage(
            role = "user",
            content = userText.trim(),
            modelUsed = _selectedModel.value
        )
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reply = geminiApiService.consultCloudStorage(
                    userPrompt = userMsg.content,
                    driveContext = driveContext,
                    model = _selectedModel.value
                )
                val modelMsg = ChatMessage(
                    role = "model",
                    content = reply,
                    modelUsed = _selectedModel.value
                )
                _messages.value = _messages.value + modelMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getInitialMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            role = "model",
            content = """
नमस्ते! मैं **OmniVault AI** हूँ — आपका व्यक्तिगत क्लाउड ड्राइव और स्टोरेज आर्किटेक्चर सलाहकार।

आप मुझसे पूछ सकते हैं:
• **Online Cloud Drive Arch:** Cloudflare R2, AWS S3, Supabase, Nextcloud की तुलना।
• **Zero Egress Savings:** बिना किसी डाउनलोड चार्ज के ड्राइव कैसे होस्ट करें।
• **Client-Side AES Encryption:** डिवाइस पर ही फाइलों को सुरक्षित कैसे करें।
• **Drive File Search:** अपनी वर्तमान फाइलों और OCR सारांश का विश्लेषण।

नीचे दिए गए सुझावों पर टैप करें या अपना प्रश्न लिखें!
            """.trimIndent(),
            modelUsed = "gemini-3.8-flash"
        )
    )
}
