package com.tracking.hardwaretracking.feature.barang.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.databinding.ActivityListBarangBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.presentation.adapter.BarangAdapter
import com.tracking.hardwaretracking.feature.hardware.HardwareActivity
import com.tracking.hardwaretracking.util.ext.gone
import com.tracking.hardwaretracking.util.ext.showToast
import com.tracking.hardwaretracking.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ListBarangActivity : AppCompatActivity() {
    private lateinit var binding : ActivityListBarangBinding
    private val viewModel : BarangViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchBarang()
        setupRecycleView()
        initObserver()
    }
    private fun setupRecycleView() {
        val foodAdapter = BarangAdapter(mutableListOf())
        foodAdapter.setItemClick(object : BarangAdapter.OnItemClick {
            override fun onClick(barangDomain: BarangDomain) {
                val intent = Intent(this@ListBarangActivity, HardwareActivity::class.java)
                    .putExtra("DETAIL_BARANG", barangDomain)
                startActivity(intent)
            }
        })

        binding.rvBarang.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(this@ListBarangActivity)
        }
    }

    private fun fetchBarang(){
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
            .onEach { foods ->
                handleFoods(foods)
            }
            .launchIn(lifecycleScope)
    }

    private fun handleFoods(barang: List<BarangDomain>) {
        binding.rvBarang.adapter?.let { barangs ->
            if (barangs is BarangAdapter) {
                barangs.updateListBarang(barang)
            }
        }
    }

    private fun handleState(state: BarangViewModel.GetBarangViewState) {
        when (state) {
            is BarangViewModel.GetBarangViewState.IsLoading -> handleLoading(state.isLoading)
            is BarangViewModel.GetBarangViewState.ShowToast -> this.showToast(state.message)
            is BarangViewModel.GetBarangViewState.Init -> Unit
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