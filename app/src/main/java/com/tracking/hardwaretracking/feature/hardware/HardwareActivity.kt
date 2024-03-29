package com.tracking.hardwaretracking.feature.hardware

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.core.LocationHelper
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.core.decodeJWT
import com.tracking.hardwaretracking.databinding.ActivityHardwareBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HardwareActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHardwareBinding
    private lateinit var locationHelper: LocationHelper

    val barang by lazy {
        intent.getParcelableExtra<BarangDomain>("DETAIL_BARANG")
    }

    @Inject
    lateinit var dataStore : TokenDataStore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHardwareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationHelper = LocationHelper(this)

        locationHelper.getCurrentLocation(object : LocationHelper.LocationListener {
            override fun onLocationReceived(city: String?) {
                binding.etCurrentLocation.isEnabled = false
                binding.etCurrentLocation.setText(city)
            }

            override fun onLocationError(errorMessage: String) {
                // Handle location retrieval errors
                println("Location error: $errorMessage")
            }
        })

        initViews()

        CoroutineScope(Dispatchers.Main).launch {
            dataStore.userName.collectLatest { user ->
                binding.tvNamaPemegang.text = user
            }
            dataStore.userRole.collectLatest { role ->
                showToast(role)
                when (role) {
                    "admin" -> {
                        binding.tvNamaPemegang.gone()
                        binding.etUser.visible()
                    }
                    "user" -> {
                        binding.tvNamaPemegang.visible()
                        binding.etUser.gone()
                    }
                }
            }
        }
    }

    private fun initViews() {
        with(binding) {
            tvNamaBarang.text = barang?.name
            tvQrCode.text = barang?.qrcode
        }
    }


}