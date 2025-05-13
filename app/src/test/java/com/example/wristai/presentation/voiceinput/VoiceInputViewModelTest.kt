package com.example.wristai.presentation.voiceinput


import com.example.wristai.openai.remote.LlmModel
import com.example.wristai.openai.remote.LlmProvider
import com.example.wristai.openai.remote.LlmRepository
import com.example.wristai.openai.remote.MultiLlmRepository
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VoiceInputViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepo: MultiLlmRepository
    private lateinit var viewModel: VoiceInputViewModel

    private val model = LlmModel("Test Model", "test-model-id", LlmProvider.CHAT_GPT)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val fakeChatGpt = mockk<LlmRepository>()
        val fakeGemini = mockk<LlmRepository>()

        coEvery { fakeChatGpt.sendPrompt(any(), any()) } returns "AI response"
        coEvery { fakeGemini.sendPrompt(any(), any()) } returns "AI response"

        fakeRepo = MultiLlmRepository(fakeChatGpt, fakeGemini)
        viewModel = VoiceInputViewModel(fakeRepo)
        viewModel.selectedModel = viewModel.modelOptions.first { it.provider == LlmProvider.CHAT_GPT }
    }


    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onUserInput should update conversation and reset error`() = runTest {
        // Run as coroutine to await properly
        val job = launch {
            viewModel.onUserInput("Hello")
        }

        advanceUntilIdle()
        job.join()

        assertEquals(listOf("Hello" to "AI response"), viewModel.conversation)
        assertNull(viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `onUserInput should set errorMessage on failure`() = runTest {
        val errorMessage = "Network error"
        coEvery { fakeRepo.sendPrompt(any(), any()) } throws RuntimeException(errorMessage)

        val job = launch {
            viewModel.onUserInput("Error test")
        }

        advanceUntilIdle()
        job.join()

        assertEquals(errorMessage, viewModel.errorMessage)
        assertTrue(viewModel.conversation.isEmpty())
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `isLoading should be true while sending prompt`() = runTest {
        val delayRepo = spyk(fakeRepo).apply {
            coEvery { sendPrompt(any(), any()) } coAnswers {
                delay(500)
                "Slow response"
            }
        }

        viewModel = VoiceInputViewModel(delayRepo)
        viewModel.selectedModel = model

        val job = launch {
            viewModel.onUserInput("Loading test")
        }

        // Let coroutine start and set isLoading = true
        yield()
        assertTrue(viewModel.isLoading)

        // Let coroutine complete
        advanceUntilIdle()
        job.join()

        assertFalse(viewModel.isLoading)
    }
}
