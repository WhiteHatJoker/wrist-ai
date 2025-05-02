package com.example.wristai.openai

data class ChatGPTRequest(
    val model: String,
    val messages: List<Map<String, String>>
)

data class ChatGPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?,
    val finish_reason: String
)

data class Message(
    val role: String,
    val content: String,
    val refusal: Any?
)
