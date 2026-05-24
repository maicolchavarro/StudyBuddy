package com.studybuddy.util

import java.security.MessageDigest

object PasswordHasher {
    fun hash(rawPassword: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(rawPassword.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
