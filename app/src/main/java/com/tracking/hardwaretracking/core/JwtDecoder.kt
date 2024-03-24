package com.tracking.hardwaretracking.core

import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.tracking.hardwaretracking.feature.login.data.dto.User

fun decodeJWT(token: String): User? {
    try {
        val decodedJWT = JWT.decode(token)
        val userId = decodedJWT.getClaim("id").asString()
        val email = decodedJWT.getClaim("email").asString()
        val name = decodedJWT.getClaim("name").asString()
        val role = decodedJWT.getClaim("role").asString()
        val iat = decodedJWT.getClaim("iat").asString()
        Log.d("TRACKTING", " BERHASIL ")
        return User(userId, email, name, role, iat)
    } catch (e: JWTDecodeException) {
        Log.d("TRACKTING", " ERROR : $e")
        e.printStackTrace()
        return null
    }
}
