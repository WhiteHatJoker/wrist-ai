package com.example.wristai.openai.remote

enum class LlmProvider { CHAT_GPT, GEMINI }

data class LlmModel(
    val displayName: String,     // shown on UI
    val modelId: String,         // sent to API
    val provider: LlmProvider
)

class MultiLlmRepository(
    private val chatGptRepo: LlmRepository,
    private val geminiRepo: LlmRepository
) : LlmRepository {

    var currentModel: LlmModel = LlmModel("ChatGPT 4o", "gpt-4o", LlmProvider.CHAT_GPT,)

    override suspend fun sendPrompt(prompt: String, model: String): String {
        return when (currentModel.provider) {
            LlmProvider.CHAT_GPT -> chatGptRepo.sendPrompt(prompt, model)
            LlmProvider.GEMINI -> geminiRepo.sendPrompt(prompt, model)
        }
    }
}