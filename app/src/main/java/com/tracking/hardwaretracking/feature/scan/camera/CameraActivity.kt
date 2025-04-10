package com.tracking.hardwaretracking.feature.scan.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.tracking.hardwaretracking.databinding.ActivityCameraBinding
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.scan.BarcodeByBarcodeState
import com.tracking.hardwaretracking.feature.scan.ScanViewModel
import com.tracking.hardwaretracking.util.ext.showGenericAlertDialog
import com.tracking.hardwaretracking.util.ext.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private val viewModel: ScanViewModel by viewModels()
    private lateinit var codeScanner: CodeScanner

    companion object {
        private const val CAMERA_REQ = 101
        const val CAMERA_RESULT = "CAMERA_RESULT"
        private const val TAG = "CameraActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
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
        Intent().apply {
            putExtra(CAMERA_RESULT, product)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun restartPreview() {
        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun decryptBase64(encodedString: String?): String? {
        return try {
            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
            String(decodedBytes, Charsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}