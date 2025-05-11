package com.example.wristai.openai.service

import com.example.wristai.BuildConfig
import com.example.wristai.openai.model.ChatGPTRequest
import com.example.wristai.openai.model.ChatGPTResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGptService {
    @Headers(
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}",
        "Content-Type: application/json"
    )
    @POST("v1/chat/completions")
    suspend fun getChatGPTResponse(@Body request: ChatGPTRequest): ChatGPTResponse
}