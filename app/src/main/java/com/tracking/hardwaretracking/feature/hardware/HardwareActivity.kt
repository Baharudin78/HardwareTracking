package com.tracking.hardwaretracking.feature.hardware

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.tracking.hardwaretracking.MainActivity
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.core.LocationHelper
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.databinding.ActivityHardwareBinding
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.request.UpdateBarangRequest
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain
import com.tracking.hardwaretracking.util.Constants.TYPE
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.showGenericAlertDialog
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HardwareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHardwareBinding
    private lateinit var locationHelper: LocationHelper
    private var adminUserSelected: String? = null
    private var hakMilikSelected: String? = null
    private var adminUserSelectedId: Int? = null
    private val viewModel: HardwareViewModel by viewModels()

    val barang by lazy {
        intent.getParcelableExtra<BarangDomain>("DETAIL_BARANG")
    }

    val type by lazy {
        intent.getIntExtra(TYPE, 0)
    }

    @Inject
    lateinit var dataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHardwareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationHelper = LocationHelper(this)
        locationHelper.getCurrentLocation(object : LocationHelper.LocationListener {
            override fun onLocationReceived(city: String?) {
                binding.etCurrentLocation.setText(city)
            }

            override fun onLocationError(errorMessage: String) {
                Log.e("LocationHelper", "Location error: $errorMessage")
            }
        })
        appBarTitle()
        initViews()
        initListener()
        observe()
    }

    private fun appBarTitle() {
        when (type) {
            1 -> {
                binding.tvTitleAppBar.text = "Take Over"
            }

            2 -> {
                binding.tvTitleAppBar.text = "Relocate"
            }
        }
    }

    private fun observe() {
        viewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleStateChange(state)
            }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: CreateProductViewState) {
        when (state) {
            is CreateProductViewState.Init -> Unit
            is CreateProductViewState.ErrorUpload -> handleErrorUpload(state.rawResponse)
            is CreateProductViewState.SuccessCreate -> handleSuccess()
            is CreateProductViewState.ShowToast -> showToast(state.message)
            is CreateProductViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleSuccess() {
        movePage()
    }

    private fun movePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }


    private fun initViews() {
        binding.tvProduct.text = barang?.name
        lifecycleScope.launch {
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
                print(e.message)
            }
        }

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
            lifecycleScope.launch {
                dataStore.userName.collectLatest { user ->
                    binding.etUser.setText(user)
                }
            }
        }
    }

    private fun observeUsers() {
        viewModel.users
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { users ->
                showDropdownUser(users)
            }
            .launchIn(lifecycleScope)
    }

    private fun showDropdownUser(users: List<UserDomain>) {
        if (type == 1) {
            // Jika tipe adalah 2, gunakan hanya nama responsiblePerson dan disable spinner
            lifecycleScope.launch {
                dataStore.userName.collectLatest { user ->
                    val responsiblePersonName = user
                    val singleUserList = listOf(responsiblePersonName)
                    val adapter = ArrayAdapter(
                        this@HardwareActivity,
                        R.layout.spinner_dropdown_item,
                        singleUserList
                    )

                    binding.spinner.adapter = adapter
                    binding.spinner.isEnabled = false // Disable spinner

                    // Set nilai yang dipilih
                    adminUserSelected = responsiblePersonName
                    dataStore.userId.collectLatest { userId ->
                        adminUserSelectedId = userId
                    }
                }
            }

            // Buat list dengan hanya satu item (responsiblePerson)

        } else {
            // Flow normal untuk tipe selain 2
            val userNames = users.map { it.name ?: "" }
            val adapter =
                ArrayAdapter(this@HardwareActivity, R.layout.spinner_dropdown_item, userNames)

            binding.spinner.adapter = adapter
            binding.spinner.isEnabled = true // Pastikan spinner diaktifkan

            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedUser = users[position]
                    adminUserSelected = selectedUser.name
                    adminUserSelectedId = selectedUser.id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No action needed
                }
            }

            // Set item yang dipilih (opsional, tergantung kebutuhan)
            // Jika ingin memilih item default, misalnya item pertama:
            if (users.isNotEmpty()) {
                binding.spinner.setSelection(0)
            }
        }
    }

    private fun handleErrorUpload(response: WrappedResponse<BarangDto>) {
        handleLoading(false)
        showGenericAlertDialog(response.message)
    }

    private fun initListener() {
        with(binding) {
            binding.appBar.setNavigationOnClickListener {
                val intent = Intent(this@HardwareActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            btnSubmit.setOnClickListener {
                lifecycleScope.launch {
                    val role = dataStore.userRole.firstOrNull() ?: ""

                    val finalHakMilik = hakMilikSelected  // Simpan dalam variabel lokal
                    val finalAdminId = adminUserSelectedId  // Simpan dalam variabel lokal
                    Log.d(
                        "DEBUG",
                        "Hak Milik Sebelum Dikirim: $finalHakMilik"
                    )  // Debugging tambahan

                    val request = UpdateBarangRequest(
                        responsiblePersonId = finalAdminId ?: barang?.responsiblePersonId,
                        currentLocation = etCurrentLocation.text.toString(),
                        descLocation = etDescriptionLocation.text.toString(),
                        qrcode = barang?.qrcode,
                        name = barang?.name,
                        categoryId = barang?.categoryId,
                        note = etNote.text.toString(),
                        hakMilik = finalHakMilik  // Gunakan variabel lokal
                    )

                    Log.d("DEBUG", "Request yang dikirim: $request")  // Debugging tambahan

                    viewModel.updateProduct(barang?.id ?: 0, request)
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }
}