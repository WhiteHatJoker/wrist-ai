package com.example.wristai.openai.remote

import com.example.wristai.openai.model.ChatGPTResponse
import com.example.wristai.openai.model.Choice
import com.example.wristai.openai.model.Message
import com.example.wristai.openai.service.ChatGptService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class ChatGptRepositoryImplTest {

    private val mockService = mockk<ChatGptService>()

    private val repository = ChatGptRepositoryImpl("fake-api-key", service = mockService)

    @Test
    fun `sendPrompt returns response content from ChatGPT`() = runTest {
        val prompt = "Hello"
        val model = "gpt-4o"

        val expectedText = "Hi there!"
        val mockResponse = ChatGPTResponse(
            choices = listOf(
                Choice(
                    message = Message(
                        content = expectedText,
                        role = "assistant", // or "user" depending on your model
                        refusal = null      // or false / "" depending on the type
                    ),
                    index = 0,
                    logprobs = null,
                    finish_reason = "stop"
                )
            )
        )

        coEvery { mockService.getChatGPTResponse(any()) } returns mockResponse

        val result = repository.sendPrompt(prompt, model)

        assertEquals(expectedText, result)
    }
}
