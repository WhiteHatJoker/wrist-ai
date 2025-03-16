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
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withContext


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
    val content: String, // Now correctly mapped as a string instead of an array
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
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { createOpenAIService() }

    // Initialize Text-to-Speech
    var textToSpeech: TextToSpeech? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    val voiceLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.let { data ->
                val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val userInput = results?.get(0) ?: "None"
                voiceText = userInput

                // Call ChatGPT API
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