package com.tracking.hardwaretracking.feature.history

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
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
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInitialViews()
        setupRecyclerView()
        initObserver()
        fetchBarang()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(mutableListOf())
        historyAdapter.setItemClick(object : HistoryAdapter.OnItemClick {
            override fun onClick(logDomain: LogDomain) {
                // Handle click event
                // Contoh: Tampilkan detail history
                showToast("Detail item: ${logDomain.barang?.name}")
            }
        })

        binding.rvHistoryList.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            // Tambahkan dekorasi untuk memberi jarak antar item
            addItemDecoration(DividerItemDecoration(this@HistoryActivity, DividerItemDecoration.VERTICAL))
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
                if (logs.isNotEmpty()) {
                    binding.ivEmpty.gone()
                    binding.tvEmpty.gone()
                    binding.tableHeader.visible()
                    binding.rvHistoryList.visible()
                    handleHistory(logs)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setupInitialViews() {
        // Sembunyikan tampilan empty state secara default
        binding.ivEmpty.gone()
        binding.tvEmpty.gone()
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
        binding.rvHistoryList.gone()
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