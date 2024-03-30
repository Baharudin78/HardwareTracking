package com.tracking.hardwaretracking.feature.login.data.mapper

import com.tracking.hardwaretracking.feature.login.data.dto.UserDto
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain

fun UserDto.toDomain() : UserDomain {
    return UserDomain(
        name = name.orEmpty(),
        email = email.orEmpty(),
        id = id.orEmpty(),
    )
}