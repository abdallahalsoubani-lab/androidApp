package com.template.analytics.di

import com.template.analytics.AnalyticsTracker
import com.template.analytics.DebugAnalyticsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker {
        // TODO: Replace with Firebase Analytics in production
        return DebugAnalyticsTracker()
    }
}
