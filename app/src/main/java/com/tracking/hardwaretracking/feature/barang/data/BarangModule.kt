package com.tracking.hardwaretracking.feature.barang.data

import com.tracking.hardwaretracking.feature.barang.data.api.BarangService
import com.tracking.hardwaretracking.feature.barang.data.repository.BarangRepositoryImpl
import com.tracking.hardwaretracking.feature.barang.domain.repository.BarangRepository
import com.tracking.hardwaretracking.module.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object BarangModule {

    @Provides
    @Singleton
    fun provideBarangService(retrofit : Retrofit): BarangService {
        return retrofit.create(BarangService::class.java)
    }

    @Singleton
    @Provides
    fun provideBarangRepository(barangService : BarangService) : BarangRepository {
        return BarangRepositoryImpl(barangService)
    }
}