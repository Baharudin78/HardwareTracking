package com.tracking.hardwaretracking.feature.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.databinding.ActivityLogBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.presentation.BarangViewModel
import com.tracking.hardwaretracking.feature.barang.presentation.adapter.BarangAdapter
import com.tracking.hardwaretracking.feature.hardware.HardwareActivity
import com.tracking.hardwaretracking.util.ext.showToast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LogActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogBinding
    private val viewModel: BarangViewModel by viewModels()
    private lateinit var barangAdapter: BarangAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()  // Setup UI first
        initObserver()       // Then setup observers
        fetchBarang()
    }

    private fun setupRecyclerView() {
        barangAdapter = BarangAdapter(mutableListOf())
        barangAdapter.setItemClick(object : BarangAdapter.OnItemClick {
            override fun onClick(barangDomain: BarangDomain) {
                navigateToDetail(barangDomain)
            }
        })

        binding.rvLog.apply {
            adapter = barangAdapter
            layoutManager = LinearLayoutManager(this@LogActivity)
            // Add optional improvements
            setHasFixedSize(true)  // Optimize RecyclerView performance
        }
    }

    private fun navigateToDetail(barangDomain: BarangDomain) {
        val intent = Intent(this, HardwareActivity::class.java).apply {
            putExtra("DETAIL_BARANG", barangDomain)
        }
        startActivity(intent)
    }

    private fun fetchBarang() {
        viewModel.fetchBarang()
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
        viewModel.barang
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { barangList ->
                Log.d("ListBarang", "Received barang list: $barangList")
                handleBarangList(barangList)
            }
            .launchIn(lifecycleScope)
    }

    private fun handleBarangList(barangList: List<BarangDomain>) {
        // Directly update adapter since we have a reference to it
        barangAdapter.updateListBarang(barangList)
    }

    private fun handleState(state: BarangViewModel.GetBarangViewState) {
        when (state) {
            is BarangViewModel.GetBarangViewState.IsLoading -> handleLoading(state.isLoading)
            is BarangViewModel.GetBarangViewState.ShowToast -> showToast(state.message)
            is BarangViewModel.GetBarangViewState.Init -> Unit
            else -> {}
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading  // Using isVisible property for cleaner syntax
    }
}