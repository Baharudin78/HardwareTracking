package com.tracking.hardwaretracking.feature.barang.data.mapper

import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain

fun BarangDto.toDomain() : BarangDomain {
    return BarangDomain(
        name = name.orEmpty(),
        responsiblePerson = responsiblePerson.orEmpty(),
        id = id.orEmpty(),
        qrcode = qrcode.orEmpty(),
        currentLocation = currentLocation.orEmpty(),
        descLocation = descLocation.orEmpty(),
        encryptQrcode = encryptQrcode.orEmpty()
    )
}