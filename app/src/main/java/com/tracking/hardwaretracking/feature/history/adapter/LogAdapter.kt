package com.tracking.hardwaretracking.feature.history.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracking.hardwaretracking.databinding.ItemHistoryBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.util.ext.toFormattedDate

class LogAdapter(
    private val log: MutableList<LogDomain>
) : RecyclerView.Adapter<LogAdapter.FoodViewHolder>() {

    interface OnItemClick {
        fun onClick(logDomain: LogDomain)
    }

    fun setItemClick(onClick: OnItemClick) {
        onClickListener = onClick
    }

    private var onClickListener: OnItemClick? = null

    fun updateLog(menu: List<LogDomain>) {
        Log.d("TESTING", "UPDATE : $menu")
        log.clear()
        log.addAll(menu)
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(logDomain: LogDomain) {
            Log.d("TESING", "BARANG adapter: $logDomain")
            binding.apply {
                tvName.text = logDomain.barang?.name
                tvLocation.text = logDomain.locationTo ?: "-"
                tvNote.text = logDomain.note
                tvDateChange.text = logDomain.createdAt?.toFormattedDate()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val inflater = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return log.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bindItem(log[position])
    }
}