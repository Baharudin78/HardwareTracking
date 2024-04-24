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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermission()
        qRScanner()
        reScan()
        observerVM()
        observeVMState()

    }

    private fun getPermission() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQ)
        }
    }

    private fun qRScanner() {
        codeScanner = CodeScanner(this, binding.scannerView)
        codeScanner.apply {
            isPreviewActive
            startPreview()
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isTouchFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    try {
                        Log.w("QRCODEE","encript : ${it.text}")
                        decryptBase64(it.text)?.let { texy ->
                            Log.w("QRCODEE", "decript : $texy")
                            viewModel.getBarcodeProduk(texy)
                        }
                    } catch (e: Exception) {
                        Log.w("ajak", "ahsdj")
                        showToast("Produk belum ada")
                    }
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.w("ajak", "ahsdj")
                    showToast("Produk belum ada")
                    Toast.makeText(
                        this@CameraActivity, "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            binding.scannerView.setOnClickListener {
                reScan()
            }
        }

    }

    private fun observerVM() {
        viewModel.barang.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { produk ->
                produk?.let { handleproduk(it) }
            }
            .launchIn(lifecycleScope)
    }

    private fun observeVMState() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach { state ->
            handleStateChange(state)
        }.launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: BarcodeByBarcodeState) {
        when (state) {
            is BarcodeByBarcodeState.ShowToast -> showToast(state.message)
            is BarcodeByBarcodeState.Error -> handleError(state.rawResponse)
            is BarcodeByBarcodeState.Success -> handleproduk(state.barcode)
            else -> {}
        }
    }

    private fun handleError(httpResponse: WrappedResponse<BarangDto>) {
        httpResponse.message?.let {
            showGenericAlertDialog(it)
        }
    }

    private fun handleproduk(barangDomain: BarangDomain) {
        val intent = Intent()
        intent.putExtra(CAMERA_RESULT, barangDomain)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun reScan() {
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

    fun decryptBase64(encodedString: String?): String? {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return decodedBytes.toString(Charsets.UTF_8)
    }

    companion object {
        const val CAMERA_REQ = 101
        const val CAMERA_RESULT = "CAMERA_RESULT"
    }
}