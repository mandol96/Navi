package com.cho.navi.util

import com.google.firebase.auth.FirebaseAuth

object AuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}