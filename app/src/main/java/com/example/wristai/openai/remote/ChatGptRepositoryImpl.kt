package com.example.wristai.openai.remote

import com.example.wristai.openai.service.ChatGptService
import com.example.wristai.openai.model.ChatGPTRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ChatGptRepositoryImpl(apiKey: String, private val service: ChatGptService? = null) : LlmRepository {
    private val actualService: ChatGptService

    init {

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.MINUTES)
            .callTimeout(2, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .build()


        actualService = service ?: Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGptService::class.java)
    }


    override suspend fun sendPrompt(prompt: String, model: String): String {
        val req = ChatGPTRequest(
            model = model,
            messages = listOf(mapOf("role" to "user", "content" to prompt))
        )
        val resp = actualService.getChatGPTResponse(req)
        return resp.choices.firstOrNull()?.message?.content ?: "No response"
    }
}
