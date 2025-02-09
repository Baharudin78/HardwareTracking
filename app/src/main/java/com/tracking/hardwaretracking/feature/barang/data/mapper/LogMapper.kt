package com.tracking.hardwaretracking.feature.barang.data.mapper

import com.tracking.hardwaretracking.feature.barang.data.dto.LogDto
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.feature.login.data.mapper.toDomain

fun LogDto.toLogDomain() : LogDomain {
    return LogDomain(
        barang = barang?.toDomain(),
        barangId = barangId ?: 0,
        createdAt = createdAt.orEmpty(),
        id = id ?: 0,
        locationFrom = locationFrom.orEmpty(),
        locationTo = locationTo.orEmpty(),
        note = note.orEmpty(),
        responsibleFrom = responsibleFrom.orEmpty(),
        responsibleFromId = responsibleFromId.orEmpty(),
        responsibleTo = responsibleTo.orEmpty(),
        responsibleToId = responsibleToId.orEmpty(),
        role = role.orEmpty(),
        status = status.orEmpty(),
        user = user?.toDomain(),
        userId = userId ?: 0

    )
}