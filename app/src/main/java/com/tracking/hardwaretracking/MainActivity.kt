package com.tracking.hardwaretracking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.databinding.ActivityMainBinding
import com.tracking.hardwaretracking.feature.barang.presentation.ListBarangActivity
import com.tracking.hardwaretracking.feature.hardware.HardwareActivity
import com.tracking.hardwaretracking.feature.login.data.dto.User
import com.tracking.hardwaretracking.feature.scan.ScanActivity
import com.tracking.hardwaretracking.util.Constants
import com.tracking.hardwaretracking.util.Constants.HOME_EXTRA
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.parcelable
import com.tracking.hardwaretracking.util.ext.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    @Inject
    lateinit var dataStore : TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {

            CoroutineScope(Dispatchers.Main).launch {
                dataStore.userRole.collectLatest { role ->
                    showToast(role)
                    when (role) {
                        "user" -> binding.btnScan.gone()
                    }
                }
            }
            btnScan.setOnClickListener {
                val intent = Intent(this@MainActivity, ScanActivity::class.java)
                startActivity(intent)
            }
            btnTakeHardware.setOnClickListener {
                startActivity(Intent(this@MainActivity, ListBarangActivity::class.java))
            }
        }
    }


}