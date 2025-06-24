package com.cho.navi.data

import com.cho.navi.util.Constants
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
                db.collection(Constants.COLLECTION_POSTS)
                    .add(post)
                    .await()
            postData.update(Constants.FIELD_ID, postData.id).await()
            postData
        }
    }

    suspend fun getLikeState(postId: String, userId: String): Result<Boolean> {
        return runCatching {
            val doc = db.collection(Constants.COLLECTION_POSTS)
                .document(postId)
                .collection(Constants.SUBCOLLECTION_LIKES)
                .document(userId)
                .get()
                .await()
            doc.exists()
        }
    }

    suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return runCatching {
            db.collection(Constants.COLLECTION_POSTS)
                .document(postId)
                .collection(Constants.SUBCOLLECTION_LIKES)
                .document(userId)
                .set(mapOf(Constants.FIELD_LIKED to true))
                .await()
        }
    }

    suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
        return runCatching {
            db.collection(Constants.COLLECTION_POSTS)
                .document(postId)
                .collection(Constants.SUBCOLLECTION_LIKES)
                .document(userId)
                .delete()
                .await()
        }
    }
}