package com.example.wristai.openai

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface OpenAiRepository {
    suspend fun sendPrompt(prompt: String): String
}

class OpenAiRepositoryImpl(apiKey: String) : OpenAiRepository {
    private val service: OpenAIService

    init {

        // Extend timeouts
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.MINUTES)
            .callTimeout(2, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .build()


        service = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)
    }



    override suspend fun sendPrompt(prompt: String): String {
        val req = ChatGPTRequest(
            model = "gpt-4o",
            messages = listOf(mapOf("role" to "user", "content" to prompt))
        )
        val resp = service.getChatGPTResponse(req)
        return resp.choices.firstOrNull()?.message?.content ?: "No response"
    }
}