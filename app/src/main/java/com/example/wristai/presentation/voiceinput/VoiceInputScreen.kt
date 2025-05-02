package com.example.wristai.presentation.voiceinput

import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun VoiceInputScreen(
    viewModel: VoiceInputViewModel = hiltViewModel()
) {
    val conversation = viewModel.conversation
    val error = viewModel.errorMessage
    val listState = rememberScalingLazyListState()
    val context = LocalContext.current

    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val userText = results?.getOrNull(0) ?: return@let
            Log.d("VoiceInput", "Recognized: $userText")
            viewModel.onUserInput(userText)
        }
    }

    val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your message")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en,ru")
    }

    ScreenScaffold {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .rotaryWithScroll(listState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (conversation.isEmpty()) {
                item {
                    Chip(
                        onClick = { voiceLauncher.launch(voiceIntent) },
                        label = "Tap to Speak"
                    )
                }
            } else {
                items(conversation) { (q, a) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("👤 $q")
                        Text("🤖 $a")
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    CompactChip(
                        onClick = { voiceLauncher.launch(voiceIntent) },
                        label = { Text("🎤 Speak") }
                    )
                }
            }
            if (error != null) {
                item {
                    Text("Error: $error", color = MaterialTheme.colors.error)
                }
            }
        }
    }
}