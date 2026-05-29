package com.skybound.space.core.di

import android.content.Context
import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.core.dispatcher.AppCoroutineDispatchers
import com.skybound.space.core.imageloader.CoilImageLoader
import com.skybound.space.core.imageloader.ImageLoader
import com.skybound.space.core.log.AppLogger
import com.skybound.space.core.log.NoOpLogger
import com.skybound.space.core.monitoring.IExceptionMonitor
import com.skybound.space.core.monitoring.IPerformanceMonitor
import com.skybound.space.core.monitoring.ITrackManager
import com.skybound.space.core.monitoring.NoOpExceptionMonitor
import com.skybound.space.core.monitoring.NoOpPerformanceMonitor
import com.skybound.space.core.monitoring.NoOpTrackManager
import com.skybound.space.core.storage.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideCoroutineDispatchers(impl: AppCoroutineDispatchers): CoroutineDispatchers = impl

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)

    @Provides
    @Singleton
    fun provideImageLoader(): ImageLoader = CoilImageLoader()

    @Provides
    @Singleton
    fun provideExceptionMonitor(): IExceptionMonitor = NoOpExceptionMonitor

    @Provides
    @Singleton
    fun providePerformanceMonitor(): IPerformanceMonitor = NoOpPerformanceMonitor

    @Provides
    @Singleton
    fun provideTrackManager(): ITrackManager = NoOpTrackManager

    @Provides
    @Singleton
    fun provideAppLogger(): AppLogger = NoOpLogger
}
