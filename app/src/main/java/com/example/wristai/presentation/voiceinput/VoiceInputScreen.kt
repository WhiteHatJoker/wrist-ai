package com.example.wristai.presentation.voiceinput

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.Locale

data class ChatGPTRequest(val model: String, val messages: List<Map<String, String>>)
data class ChatGPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?,
    val finish_reason: String
)

data class Message(
    val role: String,
    val content: String,
    val refusal: Any?
)


interface OpenAIService {
    @Headers("Authorization: Bearer REMOVED_OPENAI_API_KEY", "Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getChatGPTResponse(@Body request: ChatGPTRequest): ChatGPTResponse
}

fun createOpenAIService(): OpenAIService {
    return Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIService::class.java)
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun VoiceInputScreen(context: Context) {
    var voiceText by remember { mutableStateOf("Tap to speak") }
    var chatGPTResponse by remember { mutableStateOf("") }
    var detectedLanguage by remember { mutableStateOf(Locale.US) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { createOpenAIService() }

    // Initialize Text-to-Speech
    var textToSpeech: TextToSpeech? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US // Default to English
            }
        }
    }

    val voiceLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.let { data ->
                val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val languageCode = data.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE) // Get detected language
                val userInput = results?.get(0) ?: "None"
                voiceText = userInput

                // Detect the spoken language
                detectedLanguage = Locale.forLanguageTag(languageCode ?: "en")
                Log.d("LANGUAGE_DETECTION", "Detected Language: ${detectedLanguage.language}")

                // Call ChatGPT API with the detected language
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val request = ChatGPTRequest(
                            model = "gpt-4o",
                            messages = listOf(mapOf("role" to "user", "content" to voiceText))
                        )

                        val response = apiService.getChatGPTResponse(request)
                        val gptReply = response.choices.firstOrNull()?.message?.content ?: "No response"

                        withContext(Dispatchers.Main) {
                            chatGPTResponse = gptReply

                            // Set TTS to match the detected language
                            textToSpeech?.language = detectedLanguage
                            textToSpeech?.speak(gptReply, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            chatGPTResponse = "Error: ${e.localizedMessage}"
                        }
                        Log.e("API_ERROR", "ChatGPT API call failed", e)
                    }
                }
            }
        }

    ScreenScaffold(scrollState = scrollState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .rotaryWithScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your message")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en,ru") // Enable multi-language
            }

            Chip(
                onClick = { voiceLauncher.launch(voiceIntent) },
                label = "Voice Input",
                secondaryLabel = voiceText
            )

            Chip(
                onClick = {},
                label = "ChatGPT Response",
                secondaryLabel = chatGPTResponse
            )
        }
    }

    // Cleanup Text-to-Speech
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun VoiceInputScreenPreview() {
    VoiceInputScreen(context = androidx.compose.ui.platform.LocalContext.current)
}
