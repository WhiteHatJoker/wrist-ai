package com.example.wristai.openai.remote

import com.example.wristai.openai.service.GeminiApiService
import com.example.wristai.openai.model.Content
import com.example.wristai.openai.model.GeminiRequest
import com.example.wristai.openai.model.Part
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GeminiRepositoryImpl(private val apiKey: String, private val service: GeminiApiService? = null) : LlmRepository {
    private val actualService: GeminiApiService

    init {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.MINUTES)
            .callTimeout(2, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .build()

        actualService = service ?: Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    override suspend fun sendPrompt(prompt: String, model: String): String {
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(prompt))
                )
            )
        )

        val response = actualService.generateContent(model, apiKey, request)
        return response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text ?: "No response"
    }

}