package com.tracking.hardwaretracking.feature.barang.data.dto

import com.google.gson.annotations.SerializedName


data class ResponsiblePersonDto(
    @SerializedName("name")
    val name : String? = null,
    @SerializedName("username")
    val username : String? = null,
    @SerializedName("status")
    val status : String? = null
)
