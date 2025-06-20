package com.cho.navi.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val db: FirebaseFirestore
) {

    suspend fun addPost(
        post: Post
    ): Result<DocumentReference> {
        return runCatching {
            val postData =
            db.collection("posts")
                .add(post)
                .await()
            postData.update("id", postData.id).await()
            postData
        }
    }
}