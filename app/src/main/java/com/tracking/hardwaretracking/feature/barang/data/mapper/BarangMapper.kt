package com.tracking.hardwaretracking.feature.barang.data.mapper

import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain

fun BarangDto.toDomain() : BarangDomain {
    return BarangDomain(
        name = name.orEmpty(),
        id = id,
        qrcode = qrcode.orEmpty(),
        currentLocation = currentLocation.orEmpty(),
        descLocation = descLocation.orEmpty(),
        encryptQrcode = encryptQrcode.orEmpty(),
        createdAt = createdAt.orEmpty(),
        categoryId = categoryId ?: 0,
        hakMilik = hakMilik.orEmpty(),
        responsiblePersonId = responsiblePersonId ?: 0,
        role = role.orEmpty(),
        status = status.orEmpty(),
        updatedAt = updatedAt.orEmpty(),
        responsiblePerson = responsiblePerson?.toDomain()
    )
}