package com.tracking.hardwaretracking.feature.login.data.mapper

import com.tracking.hardwaretracking.feature.login.data.dto.UserDto
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain

fun UserDto.toDomain() : UserDomain {
    return UserDomain(
        name = name.orEmpty(),
        id = id ?: 0,
        createdAt = createdAt.orEmpty(),
        eH = eH.orEmpty(),
        flag = flag.orEmpty(),
        password = password.orEmpty(),
        role = role.orEmpty(),
        status = status.orEmpty(),
        username = username.orEmpty()
    )
}