package com.tracking.hardwaretracking.util.ext

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.isEmail() : Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Pastikan parsing dalam UTC

        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) // Bahasa Indonesia

        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this // Jika parsing gagal, kembalikan string asli
    }
}