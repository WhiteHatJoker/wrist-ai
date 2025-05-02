package com.example.wristai.presentation.voiceinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.wristai.openai.OpenAiRepository

@HiltViewModel
class VoiceInputViewModel @Inject constructor(
    private val aiRepo: OpenAiRepository
) : ViewModel() {

    var conversation by mutableStateOf<List<Pair<String, String>>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onUserInput(userText: String) {
        viewModelScope.launch {
            try {
                val aiReply = withContext(Dispatchers.IO) {
                    aiRepo.sendPrompt(userText)
                }
                conversation = conversation + (userText to aiReply)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }
}