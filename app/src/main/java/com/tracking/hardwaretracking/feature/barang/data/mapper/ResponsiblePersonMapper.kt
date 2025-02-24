package com.tracking.hardwaretracking.feature.barang.data.mapper

import com.tracking.hardwaretracking.feature.barang.data.dto.ResponsiblePersonDto
import com.tracking.hardwaretracking.feature.barang.domain.model.ResponsiblePersonDomain

fun ResponsiblePersonDto.toDomain() : ResponsiblePersonDomain {
    return ResponsiblePersonDomain(
        name = name.orEmpty(),
        username = username.orEmpty(),
        status = status.orEmpty()
    )
}