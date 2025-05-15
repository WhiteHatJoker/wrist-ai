package com.example.wristai.openai.remote

import com.example.wristai.openai.model.*
import com.example.wristai.openai.service.GeminiApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class GeminiRepositoryImplTest {

    private val mockService = mockk<GeminiApiService>()

    private val repository = GeminiRepositoryImpl("fake-api-key", service = mockService)

    @Test
    fun `sendPrompt returns response content from Gemini`() = runTest {
        val prompt = "Hello"
        val model = "gemini-2.5-pro-preview"

        val expectedText = "Hi, how can I help you?"

        val mockResponse = GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(
                            Part(text = expectedText)
                        )
                    )
                )
            )
        )

        coEvery {
            mockService.generateContent(model, "fake-api-key", any())
        } returns mockResponse

        val result = repository.sendPrompt(prompt, model)

        assertEquals(expectedText, result)
    }
}
