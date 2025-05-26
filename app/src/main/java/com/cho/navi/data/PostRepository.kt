package com.cho.navi.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val db: FirebaseFirestore
) {

    suspend fun addPost(
        category: String,
        title: String,
        description: String
    ): Result<DocumentReference> {
        return runCatching {
            val postData = hashMapOf(
                "category" to category,
                "title" to title,
                "description" to description,
                "createdAt" to Timestamp.now()
            )
            db.collection("posts")
                .add(postData)
                .await()
        }
    }
}