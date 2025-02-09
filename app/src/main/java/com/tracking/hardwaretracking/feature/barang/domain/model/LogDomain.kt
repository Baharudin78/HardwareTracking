package com.tracking.hardwaretracking.feature.barang.domain.model

import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain

data class LogDomain(
    val barang: BarangDomain?,
    val barangId: Int,
    val createdAt: String,
    val id: Int,
    val locationFrom: String,
    val locationTo: String,
    val note: String,
    val responsibleFrom: String,
    val responsibleFromId: String,
    val responsibleTo: String,
    val responsibleToId: String,
    val role: String,
    val status: String,
    val user: UserDomain?,
    val userId: Int
)
