package com.tracking.hardwaretracking.feature.history.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracking.hardwaretracking.R
import com.tracking.hardwaretracking.databinding.ItemBarangBinding
import com.tracking.hardwaretracking.databinding.ItemHistoryBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.util.ext.toFormattedDate

class HistoryAdapter(
    private val log: MutableList<LogDomain>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    interface OnItemClick {
        fun onClick(logDomain: LogDomain)
    }

    fun setItemClick(onClick: OnItemClick) {
        onClickListener = onClick
    }

    private var onClickListener: OnItemClick? = null

    fun updateLog(menu: List<LogDomain>) {
        log.clear()
        log.addAll(menu)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(logDomain: LogDomain) {
            binding.apply {
                tvName.text = logDomain.barang?.name
                tvLocation.text = logDomain.locationTo ?: "-"
                tvNote.text = logDomain.note
                tvDateChange.text = logDomain.createdAt?.toFormattedDate()

                // Add click listener to the table row
                tableContainer.setOnClickListener {
                    onClickListener?.onClick(logDomain)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return log.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bindItem(log[position])

        // Optional: Memberi warna latar belakang berbeda untuk baris genap dan ganjil
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_blue_primary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_blue_secondary))
        }
    }
}