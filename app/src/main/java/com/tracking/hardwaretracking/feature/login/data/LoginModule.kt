package com.tracking.hardwaretracking.feature.login.data

import com.tracking.hardwaretracking.feature.login.data.api.LoginService
import com.tracking.hardwaretracking.feature.login.data.repository.LoginRepositoryImpl
import com.tracking.hardwaretracking.feature.login.domain.repository.LoginRepository
import com.tracking.hardwaretracking.module.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Singleton
    @Provides
    fun provideLoginService(retrofit : Retrofit) : LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(loginApi: LoginService) : LoginRepository {
        return LoginRepositoryImpl(loginApi)
    }
}