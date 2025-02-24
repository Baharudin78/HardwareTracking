package com.tracking.hardwaretracking.feature.history

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tracking.hardwaretracking.databinding.ActivityHistoryBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.feature.history.adapter.HistoryAdapter
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter  // Declare adapter as class property

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        initObserver()
        fetchBarang()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(mutableListOf())  // Create HistoryAdapter instance
        historyAdapter.setItemClick(object : HistoryAdapter.OnItemClick {
            override fun onClick(logDomain: LogDomain) {
                // Handle click event
            }
        })

        binding.rvHistoryList.apply {
            adapter = historyAdapter  // Set the correct adapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun fetchBarang() {
        viewModel.fetchLogBarang()
    }

    private fun initObserver() {
        observeState()
        observeBarang()
    }

    private fun observeState() {
        viewModel.state
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(lifecycleScope)
    }

    private fun observeBarang() {
        viewModel.log
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { logs ->
                Log.d("TESTING", "BARANG : $logs")
                handleHistory(logs)
            }
            .launchIn(lifecycleScope)
    }

    private fun handleHistory(logs: List<LogDomain>) {
        // Directly update the adapter since we have a reference to it
        historyAdapter.updateLog(logs)
    }

    private fun handleState(state: HistoryViewModel.GetHistoryViewState) {
        when (state) {
            is HistoryViewModel.GetHistoryViewState.IsLoading -> handleLoading(state.isLoading)
            is HistoryViewModel.GetHistoryViewState.ShowToast -> this.showToast(state.message)
            is HistoryViewModel.GetHistoryViewState.Init -> Unit
            else -> {}
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }
}