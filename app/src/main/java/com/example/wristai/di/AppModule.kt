package com.example.wristai.di

import com.example.wristai.BuildConfig
import com.example.wristai.openai.OpenAiRepository
import com.example.wristai.openai.OpenAiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOpenAiRepository(): OpenAiRepository =
        OpenAiRepositoryImpl(BuildConfig.OPENAI_API_KEY)
}