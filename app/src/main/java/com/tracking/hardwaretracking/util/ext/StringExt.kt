package com.tracking.hardwaretracking.util.ext

import android.util.Patterns

fun String.isEmail() : Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}