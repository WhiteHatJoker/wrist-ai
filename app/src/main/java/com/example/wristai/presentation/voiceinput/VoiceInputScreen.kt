package com.example.wristai.presentation.voiceinput

import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.wristai.openai.remote.LlmModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll

@Composable
fun ModelSelectorDropdown(
    selectedModel: LlmModel,
    modelOptions: List<LlmModel>,
    onModelSelected: (LlmModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .background(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${selectedModel.displayName} ▼",
                style = MaterialTheme.typography.caption1
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                modelOptions.forEach { model ->
                    Text(
                        text = model.displayName,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable {
                                onModelSelected(model)
                                expanded = false
                            }
                    )
                }
            }
        }
    }
}




@OptIn(ExperimentalHorologistApi::class)
@Composable
fun VoiceInputScreen(
    viewModel: VoiceInputViewModel = hiltViewModel()
) {
    val conversation = viewModel.conversation
    val error = viewModel.errorMessage
    val isLoading    = viewModel.isLoading
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

    // Scroll to latest question upon response
    LaunchedEffect(conversation.size) {
        if (conversation.isNotEmpty()) {
            listState.scrollToItem(conversation.lastIndex * 2, scrollOffset = 0)
        }
    }

    ScreenScaffold {
        if (conversation.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotaryWithScroll(listState),
                contentAlignment = Alignment.Center
            ) {

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Chip(onClick = { voiceLauncher.launch(voiceIntent) }, label = "Tap to Speak")
                }

            }
        } else {
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
                conversation.forEachIndexed { idx, (q, a) ->
                    item {
                        Text("👤 $q")
                    }
                    item {
                        Text("🤖 $a")
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        CompactChip(
                            onClick = { voiceLauncher.launch(voiceIntent) },
                            label = { Text("🎤 Speak") }
                        )
                    }
                }


                item {
                    ModelSelectorDropdown(
                        selectedModel = viewModel.selectedModel,
                        modelOptions = viewModel.modelOptions,
                        onModelSelected = { viewModel.selectedModel = it }
                    )
                }


                if (error != null) {
                    item {
                        Text("Error: $error", color = MaterialTheme.colors.error)
                    }
                }
            }
        }
    }
}