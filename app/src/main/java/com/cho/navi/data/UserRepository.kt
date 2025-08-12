package com.cho.navi.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    suspend fun createUser() {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("로그인된 사용자가 없습니다.")

        val uid = currentUser.uid
        val userDocRef = db.collection("users").document(uid)
        val snapshot = userDocRef.get().await()

        if (!snapshot.exists()) {
            val newUser = User(
                uid = uid,
                nickname = currentUser.displayName ?: "사용자",
                profileImageUrl = currentUser.photoUrl?.toString() ?: "",
                createdAt = Timestamp.now()
            )

            userDocRef.set(newUser).await()
            Log.d("UserRepository", "User document created for uid=$uid")
        } else {
            Log.d("UserRepository", "User document already exists for uid=$uid")
        }
    }
}