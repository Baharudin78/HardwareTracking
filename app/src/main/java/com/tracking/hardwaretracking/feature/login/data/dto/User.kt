package com.tracking.hardwaretracking.feature.login.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val iat : String
) : Parcelable