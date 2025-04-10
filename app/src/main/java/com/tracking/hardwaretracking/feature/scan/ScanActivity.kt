package com.tracking.hardwaretracking.feature.scan

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.tracking.hardwaretracking.MainActivity
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.databinding.ActivityScanBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.scan.camera.CameraActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        binding.btnScanBarcode.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraResult.launch(intent)
        }

        val intent = Intent(this, CameraActivity::class.java)
        cameraResult.launch(intent)
    }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val dataMsg = result.data?.getParcelableExtra<BarangDomain>(CameraActivity.CAMERA_RESULT)
                binding.tvQrCodeEncrypted.text = "Encrypted QR : ${dataMsg?.encryptQrcode}"
                binding.tvQrCodeDecrypted.text = "QrCode : ${dataMsg?.qrcode}"
                binding.tvNamaBarang.text = "Nama Barang : ${dataMsg?.name}"
                binding.tvResponsibleName.text = "Responsible Name : ${dataMsg?.responsiblePerson?.name}"
                binding.tvLocation.text = "Location : ${dataMsg?.currentLocation}"
                binding.tvDescLocation.text = "Desc Location : ${dataMsg?.descLocation}"
            }
        }
}