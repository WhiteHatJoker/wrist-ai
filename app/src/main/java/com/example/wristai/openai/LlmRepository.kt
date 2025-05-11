package com.example.wristai.openai

interface LlmRepository {
    suspend fun sendPrompt(prompt: String, model: String): String
}
