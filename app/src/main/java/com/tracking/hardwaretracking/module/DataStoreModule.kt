package com.tracking.hardwaretracking.module

import android.content.Context
import com.tracking.hardwaretracking.core.TokenDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): TokenDataStore {
        return TokenDataStore(context)
    }
}