package com.tracking.hardwaretracking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.databinding.ActivityMainBinding
import com.tracking.hardwaretracking.feature.barang.presentation.ListBarangActivity
import com.tracking.hardwaretracking.feature.barang.presentation.TakeHardwareActivity
import com.tracking.hardwaretracking.feature.history.HistoryActivity
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain
import com.tracking.hardwaretracking.feature.login.presentation.LoginActivity
import com.tracking.hardwaretracking.feature.scan.ScanActivity
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var dataStore: TokenDataStore
    private var loginEntity: LoginDomain? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginEntity = intent.getParcelableExtra("USER") as LoginDomain?
        with(binding) {
            tvUsername.text = "Hello, ${loginEntity?.name}"
            CoroutineScope(Dispatchers.Main).launch {
                dataStore.userRole.collectLatest { role ->
                    when (role) {
                        "user" -> {
                            binding.llScan.gone()
                            binding.llTakeOver.visible()
                        }

                        "admin" -> {
                            binding.llRelocate.visible()
                        }
                    }
                }
            }
            llScan.setOnClickListener {
                val intent = Intent(this@MainActivity, ScanActivity::class.java)
                startActivity(intent)
            }
            llTakeOver.setOnClickListener {
                startActivity(Intent(this@MainActivity, TakeHardwareActivity::class.java))
            }
            llRelocate.setOnClickListener {
                startActivity(Intent(this@MainActivity, TakeHardwareActivity::class.java))
            }
            llHistory.setOnClickListener {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }
            btnLogout.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    dataStore.clearUserRole()
                    dataStore.clearUserToken()
                    dataStore.clearUserName()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }


}