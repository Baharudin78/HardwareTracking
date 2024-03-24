package com.tracking.hardwaretracking.feature.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.tracking.hardwaretracking.MainActivity
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.databinding.ActivityCustomSplashBinding
import com.tracking.hardwaretracking.feature.login.presentation.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class CustomSplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomSplashBinding
    @Inject
    lateinit var dataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {
            checkIsLoggedIn()
        }
    }

    private suspend fun checkIsLoggedIn() {
        dataStore.userTokenFlow.collect { token ->
            if (token.isEmpty()) {
                goToLoginActivity()
            } else {
                goToHomeActivity()
            }
        }
    }

    private fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goToHomeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
