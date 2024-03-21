package com.tracking.hardwaretracking.core

import com.google.gson.annotations.SerializedName

data class WrappedResponse<T> (
    @SerializedName("message") var message : String,
    @SerializedName("data") var data : T? = null
)

data class WrappedListResponse<T> (
    @SerializedName("message") var message : String,
    @SerializedName("data") var data : List<T>? = null
)