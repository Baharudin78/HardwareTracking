package com.tracking.hardwaretracking.feature.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.feature.barang.domain.usecase.GetLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getLogUseCase : GetLogUseCase
) : ViewModel(){

    private val _state = MutableStateFlow<GetHistoryViewState>(GetHistoryViewState.Init)
    val state: StateFlow<GetHistoryViewState> get() = _state


    private fun setLoading() {
        _state.value = GetHistoryViewState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = GetHistoryViewState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = GetHistoryViewState.ShowToast(message)
    }
    private val _log = MutableStateFlow<List<LogDomain>>(emptyList())
    val log: StateFlow<List<LogDomain>> get() = _log

    private fun showEmptyState() {
        _state.value = GetHistoryViewState.EmptyData
    }

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
                            if (result.data.isEmpty()) {
                                showToast("kosong")
                                showEmptyState()
                            }
                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message ?: "Error Occured")
                        }
                    }
                }
        }
    }
    sealed class GetHistoryViewState {
        object Init : GetHistoryViewState()
        data class IsLoading(val isLoading: Boolean) : GetHistoryViewState()
        data class ShowToast(val message: String) : GetHistoryViewState()
        object EmptyData : GetHistoryViewState()
    }
}