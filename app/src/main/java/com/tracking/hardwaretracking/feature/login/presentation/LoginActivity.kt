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
        binding.loginButton.setOnClickListener {
            val username = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (validate(username, password)) {
                val loginRequest = LoginRequest(username, password)
                viewModel.login(loginRequest)
            }
        }
    }


    private fun validate(email: String, password: String): Boolean {
        resetAllInputError()
        if (!email.isEmail()) {
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
        viewModel.state
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(lifecycleScope)
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
        showGenericAlertDialog(response.message)
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        if (isLoading) {
            binding.loadingProgressBar.visible()
        } else {
            binding.loadingProgressBar.gone()
        }
    }

    private suspend fun handleSuccessLogin(loginEntity: LoginDomain) {
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

        goToMainActivity()
    }


    private fun goToMainActivity() {
        startActivity(
            Intent(this@LoginActivity, MainActivity::class.java)
        )
        finish()
    }


    companion object {
        private const val TAG = "LoginActivity"
    }

}