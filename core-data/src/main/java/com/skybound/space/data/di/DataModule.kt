package com.skybound.space.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // Empty module — app layer provides concrete DB / DAO instances
    // e.g.: @Provides fun provideDb(@ApplicationContext ctx: Context): AppDatabase = ...
}
