package com.tracking.hardwaretracking.feature.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.usecase.GetDetailBarangUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val getDetailBarangUsecase: GetDetailBarangUsecase
) : ViewModel(){
    private val _state = MutableStateFlow<BarcodeByBarcodeState>(BarcodeByBarcodeState.Init)
    val mState : StateFlow<BarcodeByBarcodeState> get() = _state

    private val _barang = MutableStateFlow<BarangDomain?>(null)
    val barang: StateFlow<BarangDomain?> get() = _barang

    private fun showToast(messege : String) {
        _state.value = BarcodeByBarcodeState.ShowToast(messege)
    }

    fun getBarcodeProduk(qrcode : String) {
        viewModelScope.launch {
            getDetailBarangUsecase.getDetailBarang(qrcode).collect { result ->
                when(result) {
                    is BaseResult.Success -> _barang.value = result.data
                    else -> {}
                }
            }
        }
    }
}

private const val TAG = "BarcodeViewModel"

sealed class BarcodeByBarcodeState {
    object Init : BarcodeByBarcodeState()
    class Success(val barcode: BarangDomain) : BarcodeByBarcodeState()
    class Error(val rawResponse: WrappedResponse<BarangDto>) : BarcodeByBarcodeState()
    data class IsLoading(val isLoading: Boolean) : BarcodeByBarcodeState()
    data class ShowToast(val message : String) : BarcodeByBarcodeState()
}