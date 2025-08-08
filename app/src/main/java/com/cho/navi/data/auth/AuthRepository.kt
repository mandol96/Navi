package com.cho.navi.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth
) {

    suspend fun signInWithGoogle(googleIdToken: String): Result<FirebaseUser?> {
        return runCatching {

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            auth.signInWithCredential(firebaseCredential).await().user
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}