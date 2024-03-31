package com.tracking.hardwaretracking.feature.hardware

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tracking.hardwaretracking.MainActivity
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.core.LocationHelper
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.core.decodeJWT
import com.tracking.hardwaretracking.databinding.ActivityHardwareBinding
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.request.UpdateBarangRequest
import com.tracking.hardwaretracking.feature.barang.presentation.BarangViewModel
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.showGenericAlertDialog
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HardwareActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHardwareBinding
    private lateinit var locationHelper: LocationHelper
    private var adminUserSelected : String? = null
    private val viewModel : HardwareViewModel by viewModels()

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
        binding.etCurrentLocation.isEnabled = false
        locationHelper.getCurrentLocation(object : LocationHelper.LocationListener {
            override fun onLocationReceived(city: String?) {
                binding.etCurrentLocation.isEnabled = false
                binding.etCurrentLocation.setText(city)
            }

            override fun onLocationError(errorMessage: String) {
                println("Location error: $errorMessage")
            }
        })

        initViews()
        initListener()
        observe()
    }

    private fun observe(){
        viewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleStateChange(state)
            }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: CreateProductViewState){
        when(state){
            is CreateProductViewState.Init -> Unit
            is CreateProductViewState.ErrorUpload -> handleErrorUpload(state.rawResponse)
            is CreateProductViewState.SuccessCreate -> handleSuccess()
            is CreateProductViewState.ShowToast -> showToast(state.message)
            is CreateProductViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun observeUsers() {
        viewModel.users.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { users ->
                showDropdownUser(users)
            }
            .launchIn(lifecycleScope)
    }

    private fun handleSuccess(){
        movePage()
    }

    private fun handleErrorUpload(response: WrappedResponse<BarangDto>){
        handleLoading(false)
        showGenericAlertDialog(response.message)
    }

    private fun movePage(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun initViews() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                dataStore.userRole.collectLatest { role ->
                    when (role) {
                        "admin" -> {
                            initAdminMenu()
                        }
                        "user" -> {
                            initUserMenu()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("initViews", "Error collecting DataStore values: ${e.message}")
            }
        }
        binding.tvNamaBarang.text = "Nama Barang : ${barang?.name}"
        binding.tvQrCode.text = "Qrcode : ${barang?.qrcode}"
        binding.tvCurrentLocation.text = "Lokasi Sekarang : ${barang?.currentLocation}"
        binding.tvDescLocation.text = "Lokasi Deskripsi : ${barang?.descLocation}"
        binding.tvResponsibleperson.text = "Pemegang saat ini : ${barang?.responsiblePerson}"

    }


    private fun initAdminMenu() {
        with(binding) {
            nameInput.gone()
            spinner.visible()
            observeUsers()
        }
    }
    private fun initUserMenu() {
        with(binding) {
            spinner.gone()
            nameInput.visible()
            etUser.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                dataStore.userName.collectLatest { user ->
                    binding.etUser.setText(user)
                }
            }
        }
    }

    private fun showDropdownUser(users: List<UserDomain>) {
        val userNames = users.map { it.name ?: "" }
        val adapter = ArrayAdapter(this@HardwareActivity, android.R.layout.simple_spinner_dropdown_item, userNames)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedUser = users[position]
                adminUserSelected = selectedUser.name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }
    }

    private fun initListener() {
        with(binding) {
            btnSubmit.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    dataStore.userRole.collectLatest { role ->
                        val request = if (role == "admin") {
                            UpdateBarangRequest(
                                name = tvNamaBarang.text.toString(),
                                qrcode = tvQrCode.text.toString(),
                                responsiblePerson = adminUserSelected,
                                currentLocation = etCurrentLocation.text.toString(),
                                descLocation = etDescriptionLocation.text.toString(),
                            )
                        } else {
                            Log.d("HARDWARE asu bansatt", "userr")
                            UpdateBarangRequest(
                                name = tvNamaBarang.text.toString(),
                                qrcode = tvQrCode.text.toString(),
                                responsiblePerson = etUser.text.toString(),
                                currentLocation = etCurrentLocation.text.toString(),
                                descLocation = etDescriptionLocation.text.toString(),
                            )
                        }
                        viewModel.updateProduct(barang?.id.orEmpty(), request)
                    }
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visible()
        }else{
            binding.progressBar.gone()
        }
    }

}