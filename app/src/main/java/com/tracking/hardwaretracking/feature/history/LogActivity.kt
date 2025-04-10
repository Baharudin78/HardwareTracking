package com.tracking.hardwaretracking.feature.history

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tracking.hardwaretracking.core.TokenDataStore
import com.tracking.hardwaretracking.databinding.ActivityLogBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.feature.history.adapter.LogAdapter
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LogActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogBinding
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: LogAdapter
    @Inject
    lateinit var tokenDataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInitialViews()
        setupRecyclerView()  // Setup UI first
        initObserver()       // Then setup observers
        fetchBarang()
    }

    private fun setupRecyclerView() {
        historyAdapter = LogAdapter(mutableListOf())  // Create HistoryAdapter instance
        historyAdapter.setItemClick(object : LogAdapter.OnItemClick {
            override fun onClick(logDomain: LogDomain) {
                // Handle click event
            }
        })

        binding.rvLog.apply {
            adapter = historyAdapter  // Set the correct adapter
            layoutManager = LinearLayoutManager(this@LogActivity)
        }
    }

    private fun fetchBarang() {
        viewModel.fetchLogBarang()
    }

    private fun setupInitialViews() {
        // Sembunyikan tampilan empty state secara default
        binding.ivEmpty.gone()
        binding.tvEmpty.gone()
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
        lifecycleScope.launch {
            val userIdLogin = tokenDataStore.userId.firstOrNull() ?: 0
            viewModel.log
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { logs ->
                    val filteredLogs = logs.filter { it.userId == userIdLogin }
                    Log.d("LOGS", " FILTERED : $filteredLogs")
                    if (filteredLogs.isEmpty()) {
                        // Jika hasil filter kosong, tampilkan empty state
                        binding.ivEmpty.visible()
                        binding.tvEmpty.visible()
                        binding.tableHeader.gone()
                        binding.rvLog.gone()
                    } else {
                        // Jika hasil filter ada, tampilkan data
                        binding.ivEmpty.gone()
                        binding.tvEmpty.gone()
                        binding.tableHeader.visible()
                        binding.rvLog.visible()
                        handleHistory(filteredLogs)
                    }
                }
        }
    }

    private fun handleHistory(logs: List<LogDomain>) {
        historyAdapter.updateLog(logs)
    }

    private fun handleState(state: HistoryViewModel.GetHistoryViewState) {
        when (state) {
            is HistoryViewModel.GetHistoryViewState.IsLoading -> handleLoading(state.isLoading)
            is HistoryViewModel.GetHistoryViewState.ShowToast -> this.showToast(state.message)
            is HistoryViewModel.GetHistoryViewState.Init -> Unit
            is HistoryViewModel.GetHistoryViewState.EmptyData -> handleEmptyState()
            else -> {}
        }
    }

    private fun handleEmptyState() {
        binding.ivEmpty.visible()
        binding.tvEmpty.visible()
        binding.tableHeader.gone()
        binding.rvLog.gone()
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
            binding.ivEmpty.gone()
            binding.tvEmpty.gone()
        } else {
            binding.progressBar.gone()
        }
    }
}