package com.tracking.hardwaretracking.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain
import com.tracking.hardwaretracking.feature.login.domain.request.LoginRequest
import com.tracking.hardwaretracking.feature.login.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(){

    private val _state = MutableStateFlow<LoginViewState>(LoginViewState.Init)
    val state: StateFlow<LoginViewState> get() = _state

    private fun setLoading() {
        _state.value = LoginViewState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = LoginViewState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = LoginViewState.ShowToast(message)
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            loginUseCase.invoke(loginRequest)
                .onStart { showLoading() }
                .catch { exception -> handleException(exception) }
                .collect { handleResult(it) }
        }
    }

    private fun showLoading() {
        viewModelScope.launch {
            _state.value = LoginViewState.IsLoading(true)
             delay(500)
        }
    }

    private fun handleException(exception: Throwable) {
        hideLoading()
        showToast(exception.message.toString())
    }

    private fun handleResult(baseResult: BaseResult<LoginDomain, WrappedResponse<LoginDto>>) {
        hideLoading()
        when (baseResult) {
            is BaseResult.Error -> _state.value = LoginViewState.ErrorLogin(baseResult.rawResponse)
            is BaseResult.Success -> _state.value = LoginViewState.SuccessLogin(baseResult.data)
        }
    }

}

sealed class LoginViewState {
    object Init : LoginViewState()
    data class IsLoading(val isLoading: Boolean) : LoginViewState()
    data class ShowToast(val message: String) : LoginViewState()
    data class SuccessLogin(val loginEntity: LoginDomain) : LoginViewState()
    data class ErrorLogin(val rawResponse: WrappedResponse<LoginDto>) : LoginViewState()
}