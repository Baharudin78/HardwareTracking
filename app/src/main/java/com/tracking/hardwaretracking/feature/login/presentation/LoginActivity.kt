package com.tracking.hardwaretracking.feature.login.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.tracking.hardwaretracking.MainActivity
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.core.decodeJWT
import com.tracking.hardwaretracking.databinding.ActivityLoginBinding
import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain
import com.tracking.hardwaretracking.feature.login.domain.request.LoginRequest
import com.tracking.hardwaretracking.util.Constants.HOME_EXTRA
import com.tracking.hardwaretracking.util.Constants.MIN_PASSWORD_LENGTH
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.isEmail
import com.tracking.hardwaretracking.util.ext.setSafeOnClickListener
import com.tracking.hardwaretracking.util.ext.showGenericAlertDialog
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var dataStore: TokenDataStore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        login()
        observe()
    }

    private fun login() {
        binding.loginButton.setSafeOnClickListener {
            val username = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (validate(username, password)) {
                val loginRequest = LoginRequest(username, password)
                viewModel.login(loginRequest)
            }
        }
    }


    private fun validate(username: String, password: String): Boolean {
        resetAllInputError()
        if (!username.isNotEmpty()) {
            setEmailError(getString(R.string.error_email_not_valid))
            return false
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            setPasswordError(getString(R.string.error_password_not_valid))
            return false
        }

        return true
    }

    private fun resetAllInputError() {
        setEmailError(null)
        setPasswordError(null)
    }

    private fun setEmailError(e: String?) {
        binding.emailInput.error = e
    }

    private fun setPasswordError(e: String?) {
        binding.passwordInput.error = e
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.state
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state -> handleStateChange(state) }
        }
    }

    private suspend fun handleStateChange(state: LoginViewState) {
        when (state) {
            is LoginViewState.Init -> Unit
            is LoginViewState.ErrorLogin -> handleErrorLogin(state.rawResponse)
            is LoginViewState.SuccessLogin -> handleSuccessLogin(state.loginEntity)
            is LoginViewState.ShowToast -> showToast(state.message)
            is LoginViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorLogin(response: WrappedResponse<LoginDto>) {
        showGenericAlertDialog("Username atau Password Salah")
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        if (isLoading) {
            binding.loadingProgressBar.visible()
            binding.loadingProgressBar.isIndeterminate = true
        } else {
            binding.loadingProgressBar.gone()
        }
    }

    private suspend fun handleSuccessLogin(loginEntity: LoginDomain) {
        when (loginEntity.role) {
            "super admin" -> {
                showToast("Super admin tidak bisa login")
            }

            else -> {
                loginEntity.token.takeIf { it.isNotEmpty() }?.let { token ->
                    coroutineScope {
                        launch {
                            dataStore.saveUserToken(token)
                        }
                    }
                }
                loginEntity.role.takeIf { it.isNotEmpty() }?.let { role ->
                    coroutineScope {
                        launch {
                            dataStore.saveRoleLogin(role)
                        }
                    }
                }
                loginEntity.name.takeIf { it.isNotEmpty() }?.let { name ->
                    coroutineScope {
                        launch {
                            dataStore.saveNameUser(name)
                        }
                    }
                }
                loginEntity.id.takeIf { it != 0 }?.let { id ->
                    coroutineScope {
                        launch {
                            dataStore.saveUserIdLogin(id)
                        }
                    }
                }

                goToMainActivity(loginEntity)
            }
        }
    }


    private fun goToMainActivity(loginEntity: LoginDomain) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("USER", loginEntity)
        }
        startActivity(intent)
        // finishAffinity() tidak diperlukan karena FLAG_ACTIVITY_CLEAR_TASK sudah membersihkan stack
    }


    companion object {
        private const val TAG = "LoginActivity"
    }

}