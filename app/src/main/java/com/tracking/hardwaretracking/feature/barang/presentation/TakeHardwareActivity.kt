package com.tracking.hardwaretracking.feature.barang.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.databinding.ActivityTakeHardwareBinding
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.hardware.HardwareActivity
import com.tracking.hardwaretracking.feature.scan.BarcodeByBarcodeState
import com.tracking.hardwaretracking.feature.scan.ScanViewModel
import com.tracking.hardwaretracking.util.ext.showGenericAlertDialog
import com.tracking.hardwaretracking.util.ext.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.util.Base64
import com.tracking.hardwaretracking.util.Constants.TYPE

@AndroidEntryPoint
class TakeHardwareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTakeHardwareBinding
    private val viewModel: ScanViewModel by viewModels()
    private lateinit var codeScanner: CodeScanner

    val typeMenu by lazy {
        intent.getIntExtra(TYPE, 0)
    }
    companion object {
        private const val CAMERA_REQ = 101
        const val CAMERA_RESULT = "CAMERA_RESULT"
        const val DETAIL_BARANG = "DETAIL_BARANG"
        private const val TAG = "TakeHardwareActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeHardwareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCameraPermission()
        setupQRScanner()
        setupObservers()
    }

    private fun setupCameraPermission() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQ)
        }
    }

    private fun setupQRScanner() {
        codeScanner = CodeScanner(this, binding.scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isTouchFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback { result ->
                runOnUiThread {
                    handleScanResult(result.text)
                }
            }

            errorCallback = ErrorCallback { error ->
                runOnUiThread {
                    showToast(getString(R.string.camera_initialization_error))
                }
            }
        }

        binding.scannerView.setOnClickListener {
            restartPreview()
        }

        startPreview()
    }

    private fun handleScanResult(scanResult: String) {
        try {
            decryptBase64(scanResult)?.let { decryptedText ->
                viewModel.getBarcodeProduk(decryptedText)
            } ?: run {
                showToast(getString(R.string.invalid_qr_code))
            }
        } catch (e: Exception) {
            showToast(getString(R.string.product_not_found))
        }
    }

    private fun setupObservers() {
        // Observe product data
        viewModel.barang
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { product ->
                product?.let { handleProduct(it) }
            }
            .launchIn(lifecycleScope)

        // Observe state changes
        viewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: BarcodeByBarcodeState) {
        when (state) {
            is BarcodeByBarcodeState.ShowToast -> showToast(state.message)
            is BarcodeByBarcodeState.Error -> handleError(state.rawResponse)
            is BarcodeByBarcodeState.Success -> handleProduct(state.barcode)
            else -> { /* Ignore other states */ }
        }
    }

    private fun handleError(httpResponse: WrappedResponse<BarangDto>) {
        httpResponse.message?.let { errorMessage ->
            showGenericAlertDialog(errorMessage)
        }
    }

    private fun handleProduct(product: BarangDomain) {
        Intent(this, HardwareActivity::class.java).apply {
            putExtra(DETAIL_BARANG, product)
            putExtra(TYPE, typeMenu)
            startActivity(this)
        }
        finish()
    }

    private fun restartPreview() {
        codeScanner.startPreview()
    }

    private fun startPreview() {
        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    private fun decryptBase64(encodedString: String?): String? {
        return try {
            encodedString?.let {
                val decodedBytes = Base64.decode(it, Base64.DEFAULT)
                String(decodedBytes, Charsets.UTF_8)
            }
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}