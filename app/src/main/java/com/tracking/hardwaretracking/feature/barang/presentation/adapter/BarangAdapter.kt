package com.tracking.hardwaretracking.feature.barang.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracking.hardwaretracking.databinding.ItemBarangBinding
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain

class BarangAdapter(
    private val barang: MutableList<BarangDomain>
) : RecyclerView.Adapter<BarangAdapter.FoodViewHolder>() {

    interface OnItemClick {
        fun onClick(barangDomain: BarangDomain)
    }

    fun setItemClick(onClick: OnItemClick) {
        onClickListener = onClick
    }

    private var onClickListener: OnItemClick? = null

    fun updateListBarang(menu: List<BarangDomain>) {
        barang.clear()
        barang.addAll(menu)
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(private val binding: ItemBarangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(barangDomain: BarangDomain) {
            binding.apply {
                tvCurrentLoc.text = "Lokasi : ${barangDomain.currentLocation}"
                tvQrCode.text = "EncryptedQR : ${barangDomain.qrcode}"
                tvNamaBarang.text = "Nama barang : ${barangDomain.name}"
                tvNamaPemegang.text = "PIC :${barangDomain.responsiblePerson?.username}"
                root.setOnClickListener {
                    onClickListener?.onClick(barangDomain)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val inflater = ItemBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return barang.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bindItem(barang[position])
    }
}