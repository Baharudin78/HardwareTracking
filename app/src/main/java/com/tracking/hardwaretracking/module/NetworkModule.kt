package com.tracking.hardwaretracking.module

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.tracking.hardwaretracking.core.RequestInterceptor
import com.tracking.hardwaretracking.core.SharedPrefs
import com.tracking.hardwaretracking.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder().apply {
            addConverterFactory(GsonConverterFactory.create())
            client(okHttpClient)
            baseUrl(BASE_URL)
        }.build()

    }

    @Provides
    @Singleton
    fun providesChuckInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        return ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .redactHeaders("**")
            .alwaysReadResponseBody(true)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        requestInterceptor: RequestInterceptor,
        chuckerInterceptor : ChuckerInterceptor
    ) : OkHttpClient{
        return OkHttpClient.Builder().apply {
            connectTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            addInterceptor(requestInterceptor)
            addInterceptor(chuckerInterceptor)
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }

    @Provides
    fun requestInterceptor(pref : SharedPrefs) : RequestInterceptor{
        return RequestInterceptor(pref)
    }
}