package com.example.shilpakala.utils

import java.security.MessageDigest
import javax.inject.Inject

class PasswordHasher @Inject constructor() {
    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
