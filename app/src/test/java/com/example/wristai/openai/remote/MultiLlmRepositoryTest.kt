package com.example.wristai.openai.remote

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MultiLlmRepositoryTest {
    @Test
    fun `sendPrompt delegates to correct repo based on provider`() = runTest {
        val chatGpt = mockk<LlmRepository>()
        val gemini = mockk<LlmRepository>()

        val multiRepo = MultiLlmRepository(chatGpt, gemini)

        val prompt = "Hi"
        val model = "gpt-4o"
        multiRepo.currentModel = LlmModel("ChatGPT", model, LlmProvider.CHAT_GPT)

        coEvery { chatGpt.sendPrompt(prompt, model) } returns "ChatGPT reply"

        val result = multiRepo.sendPrompt(prompt, model)

        assertEquals("ChatGPT reply", result)
        coVerify { chatGpt.sendPrompt(prompt, model) }
        coVerify(exactly = 0) { gemini.sendPrompt(any(), any()) }
    }

}