package com.tracking.hardwaretracking.feature.login.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginDomain(
    val token : String,
    val role : String,
    val name : String,
    val email : String,
    val id : String
) :Parcelable
