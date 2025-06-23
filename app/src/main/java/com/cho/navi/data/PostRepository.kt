package com.cho.navi.data

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

    suspend fun getLikeState(postId: String, userId: String): Result<Boolean> {
        return runCatching {
            val doc = db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .get()
                .await()
            doc.exists()
        }
    }

    suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return runCatching {
            db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .set(mapOf("liked" to true))
                .await()
        }
    }

    suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
        return runCatching {
            db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .delete()
                .await()
        }
    }
}