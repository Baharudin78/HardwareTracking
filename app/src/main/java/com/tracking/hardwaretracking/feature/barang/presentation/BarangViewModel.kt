package com.tracking.hardwaretracking.feature.barang.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.feature.barang.domain.usecase.BarangUseCase
import com.tracking.hardwaretracking.feature.barang.domain.usecase.GetLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarangViewModel @Inject constructor(
    private val barangUseCase : BarangUseCase,
    private val getLogUseCase: GetLogUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<GetBarangViewState>(GetBarangViewState.Init)
    val state: StateFlow<GetBarangViewState> get() = _state

    private val _barang = MutableStateFlow<List<BarangDomain>>(emptyList())
    val barang: StateFlow<List<BarangDomain>> get() = _barang

    private fun setLoading() {
        _state.value = GetBarangViewState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = GetBarangViewState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = GetBarangViewState.ShowToast(message)
    }

    init {
        fetchBarang()
        fetchLogBarang()
    }

    fun fetchBarang() {
        viewModelScope.launch {
            barangUseCase.getBarang()
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
                            _barang.value = result.data
                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message ?: "Error Occured")
                        }
                    }
                }
        }
    }

    private val _log = MutableStateFlow<List<LogDomain>>(emptyList())
    val log: StateFlow<List<LogDomain>> get() = _log

    fun fetchLogBarang() {
        viewModelScope.launch {
            getLogUseCase.getLog()
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
                            _log.value = result.data
                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message ?: "Error Occured")
                        }
                    }
                }
        }
    }

    sealed class GetBarangViewState {
        object Init : GetBarangViewState()
        data class IsLoading(val isLoading: Boolean) : GetBarangViewState()
        data class ShowToast(val message: String) : GetBarangViewState()
    }
}