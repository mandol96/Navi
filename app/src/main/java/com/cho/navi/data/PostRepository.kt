package com.cho.navi.data

import android.net.Uri
import com.cho.navi.util.FirestoreConstants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostRepository(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    suspend fun addPost(
        post: Post,
        imageUris: List<Uri>
    ): Result<DocumentReference> {
        return runCatching {
            val imageUrls = mutableListOf<String>()

            val currentUser = auth.currentUser

            for (uri in imageUris) {
                val fileName = "post_images/${UUID.randomUUID()}.jpg"
                val imageRef = storage.reference.child(fileName)

                imageRef.putFile(uri).await()
                val downloadUrl = imageRef.downloadUrl.await().toString()
                imageUrls.add(downloadUrl)
            }

            val postWithImages = post.copy(
                imageUrls = imageUrls,
                nickName = currentUser?.displayName ?: "소금빵",
                createdAt = Timestamp.now(),
            )

            val postData =
                db.collection(FirestoreConstants.COLLECTION_POSTS)
                    .add(postWithImages)
                    .await()
            postData.update(FirestoreConstants.FIELD_ID, postData.id).await()
            postData
        }
    }

    suspend fun getLikeState(postId: String, userId: String): Result<Boolean> {
        return runCatching {
            val doc = db.collection(FirestoreConstants.COLLECTION_POSTS)
                .document(postId)
                .collection(FirestoreConstants.SUBCOLLECTION_LIKES)
                .document(userId)
                .get()
                .await()
            doc.exists()
        }
    }

    suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return runCatching {
            db.collection(FirestoreConstants.COLLECTION_POSTS)
                .document(postId)
                .collection(FirestoreConstants.SUBCOLLECTION_LIKES)
                .document(userId)
                .set(mapOf(FirestoreConstants.FIELD_LIKED to true))
                .await()
        }
    }

    suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
        return runCatching {
            db.collection(FirestoreConstants.COLLECTION_POSTS)
                .document(postId)
                .collection(FirestoreConstants.SUBCOLLECTION_LIKES)
                .document(userId)
                .delete()
                .await()
        }
    }
}