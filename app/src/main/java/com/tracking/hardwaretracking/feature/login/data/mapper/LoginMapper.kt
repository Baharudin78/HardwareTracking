package com.tracking.hardwaretracking.feature.login.data.mapper

import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain

fun LoginDto.toLoginEntity(): LoginDomain {
    return LoginDomain(
        token = token.orEmpty(),
        role = role.orEmpty(),
        name = name.orEmpty(),
        email = email.orEmpty(),
        id = id ?: 0,
    )
}