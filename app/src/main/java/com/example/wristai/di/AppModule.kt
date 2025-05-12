package com.example.wristai.di

import com.example.wristai.BuildConfig
import com.example.wristai.openai.remote.ChatGptRepositoryImpl
import com.example.wristai.openai.remote.GeminiRepositoryImpl
import com.example.wristai.openai.remote.LlmRepository
import com.example.wristai.openai.remote.MultiLlmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("chatGpt")
    fun provideChatGptRepo(): LlmRepository =
        ChatGptRepositoryImpl(BuildConfig.OPENAI_API_KEY)

    @Provides
    @Singleton
    @Named("gemini")
    fun provideGeminiRepo(): LlmRepository =
        GeminiRepositoryImpl(BuildConfig.GEMINI_API_KEY)

    @Provides
    @Singleton
    fun provideMultiRepo(
        @Named("chatGpt") chatGpt: LlmRepository,
        @Named("gemini") gemini: LlmRepository
    ): MultiLlmRepository =
        MultiLlmRepository(chatGpt, gemini)
}
