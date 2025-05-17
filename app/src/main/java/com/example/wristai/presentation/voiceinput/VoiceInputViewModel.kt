package com.example.wristai.presentation.voiceinput

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.wristai.openai.remote.LlmModel
import com.example.wristai.openai.remote.LlmProvider
import com.example.wristai.openai.remote.MultiLlmRepository

@HiltViewModel
class VoiceInputViewModel @Inject constructor(
    private val aiRepo: MultiLlmRepository
) : ViewModel() {

    var conversation by mutableStateOf<List<Pair<String, String>>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    val modelOptions = listOf(
        LlmModel("ChatGPT 4o", "gpt-4o", LlmProvider.CHAT_GPT),
        LlmModel("ChatGPT 4.1", "gpt-4.1", LlmProvider.CHAT_GPT),
        LlmModel("ChatGPT o4-mini", "o4-mini", LlmProvider.CHAT_GPT),
        LlmModel("Gemini 2.5 Pro", "gemini-2.5-pro-preview-05-06", LlmProvider.GEMINI),
        LlmModel("Gemini 2.5 Flash", "gemini-2.5-flash-preview-04-17", LlmProvider.GEMINI),
        LlmModel("Gemini 2.0 Flash", "gemini-2.0-flash", LlmProvider.GEMINI),
    )

    var selectedModel by mutableStateOf(modelOptions[0])


    fun onUserInput(userText: String) {
        errorMessage = null
        isLoading = true

//        Log.d("VoiceInputVM", "🗣️ onUserInput(\"$userText\") called")
        viewModelScope.launch {
            try {
//                Log.d("VoiceInputVM", "➡️ Sending prompt to ${selectedModel.modelId}…")
                aiRepo.currentModel = selectedModel // <-- update model/provider
                val aiReply = aiRepo.sendPrompt(userText, selectedModel.modelId)
//                Log.d("VoiceInputVM", "🟢 AI Reply: ${aiReply.take(200)}…")
                conversation = conversation + (userText to aiReply)
                } catch (e: Exception) {
//                    Log.e("VoiceInputVM", "❌ Error:", e)
                    errorMessage = when {
                        e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true ->
                            "Invalid API keys"
                        e.message?.contains("403") == true ->
                            "Access denied. No permission to use this model."
                        else ->
                            e.localizedMessage ?: "Unknown error occurred."
                    }
                } finally {
                isLoading = false
            }
        }
    }

}