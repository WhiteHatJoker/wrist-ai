package com.example.wristai.openai

import com.example.wristai.BuildConfig
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers(
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}",
        "Content-Type: application/json"
    )
    @POST("v1/chat/completions")
    suspend fun getChatGPTResponse(@Body request: ChatGPTRequest): ChatGPTResponse
}