package com.tracking.hardwaretracking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.databinding.ActivityMainBinding
import com.tracking.hardwaretracking.feature.barang.presentation.ListBarangActivity
import com.tracking.hardwaretracking.feature.barang.presentation.TakeHardwareActivity
import com.tracking.hardwaretracking.feature.history.HistoryActivity
import com.tracking.hardwaretracking.feature.history.LogActivity
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain
import com.tracking.hardwaretracking.feature.login.presentation.LoginActivity
import com.tracking.hardwaretracking.feature.scan.ScanActivity
import com.tracking.hardwaretracking.util.Constants.TYPE
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
            lifecycleScope.launch {
                dataStore.userName.collectLatest { name ->
                    tvUsername.text = "Hello,$name "
                }
            }
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
                startActivity(
                    Intent(
                        this@MainActivity,
                        TakeHardwareActivity::class.java
                    ).putExtra(TYPE, 1)
                )
            }
            llRelocate.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        TakeHardwareActivity::class.java
                    ).putExtra(TYPE, 2)
                )
            }
            llHistory.setOnClickListener {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }
            llLog.setOnClickListener {
                startActivity(Intent(this@MainActivity, LogActivity::class.java))
            }
            btnLogout.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    dataStore.clearUserRole()
                    dataStore.clearUserToken()
                    dataStore.clearUserName()
                    goToLogin()
                }
            }
        }
    }
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}