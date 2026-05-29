package com.skybound.space.core.di

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.core.imageloader.ImageLoader
import com.skybound.space.core.monitoring.ITrackManager
import com.skybound.space.core.storage.DataStoreManager

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CoreEntryPoint {
    fun coroutineDispatchers(): CoroutineDispatchers
    fun dataStoreManager(): DataStoreManager
    fun imageLoader(): ImageLoader
    fun trackManager(): ITrackManager
}

object AppInjector {
    fun from(context: Context): CoreEntryPoint =
        EntryPoints.get(context.applicationContext, CoreEntryPoint::class.java)
}
