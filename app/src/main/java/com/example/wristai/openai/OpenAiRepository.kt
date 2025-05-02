package com.example.wristai.openai

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface OpenAiRepository {
    suspend fun sendPrompt(prompt: String): String
}

class OpenAiRepositoryImpl(apiKey: String) : OpenAiRepository {
    private val service: OpenAIService = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIService::class.java)

    override suspend fun sendPrompt(prompt: String): String {
        val req = ChatGPTRequest(
            model = "gpt-4o",
            messages = listOf(mapOf("role" to "user", "content" to prompt))
        )
        val resp = service.getChatGPTResponse(req)
        return resp.choices.firstOrNull()?.message?.content ?: "No response"
    }
}