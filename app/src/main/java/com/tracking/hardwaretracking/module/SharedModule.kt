package com.tracking.hardwaretracking.module

import android.content.Context
import com.tracking.hardwaretracking.core.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SharedModule {
    @Provides
    fun provideSharedPref(@ApplicationContext context : Context) : SharedPrefs{
        return SharedPrefs(context)
    }
}