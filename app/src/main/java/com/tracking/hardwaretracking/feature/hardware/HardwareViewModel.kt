package com.tracking.hardwaretracking.feature.hardware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.request.UpdateBarangRequest
import com.tracking.hardwaretracking.feature.barang.domain.usecase.GetUserUsecase
import com.tracking.hardwaretracking.feature.barang.domain.usecase.UpdateBarangUsecase
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HardwareViewModel @Inject constructor(
    private val getListUserUsecase: GetUserUsecase,
    private val updateBarangUsecase: UpdateBarangUsecase
) : ViewModel() {

    private val state = MutableStateFlow<CreateProductViewState>(CreateProductViewState.Init)
    val mState: StateFlow<CreateProductViewState> get() = state

    private val _users = MutableStateFlow<List<UserDomain>>(emptyList())
    val users: StateFlow<List<UserDomain>> get() = _users

    private fun setLoading() {
        state.value = CreateProductViewState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = CreateProductViewState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = CreateProductViewState.ShowToast(message)
    }

    private fun successCreate() {
        state.value = CreateProductViewState.SuccessCreate
    }

    init {
        fetchUser()
    }
    fun updateProduct(
        id: String,
        request : UpdateBarangRequest
    ) {
        viewModelScope.launch {
            updateBarangUsecase.updateBarang(id, request)
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showToast(exception.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {
                            hideLoading()
                            successCreate()
                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message)
                        }
                    }
                }
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            getListUserUsecase.getUser()
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showToast(exception.message ?: "Error Occured")
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {
                            _users.value = result.data
                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message ?: "Error Occured")
                        }
                    }
                }
        }
    }
}

sealed class CreateProductViewState {
    object Init : CreateProductViewState()
    object SuccessCreate : CreateProductViewState()
    data class IsLoading(val isLoading: Boolean) : CreateProductViewState()
    data class ShowToast(val message: String) : CreateProductViewState()
    data class ErrorUpload(val rawResponse: WrappedResponse<BarangDto>) :
        CreateProductViewState()

}