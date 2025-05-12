package com.example.wristai.openai.remote

interface LlmRepository {
    suspend fun sendPrompt(prompt: String, model: String): String
}
