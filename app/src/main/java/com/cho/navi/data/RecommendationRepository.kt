package com.cho.navi.data

import com.cho.navi.util.FirestoreConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecommendationRepository(
    private val db: FirebaseFirestore
) {

    suspend fun fetchPosts(): Result<List<Post>> {
        return kotlin.runCatching {
            val snapshot = db.collection(FirestoreConstants.COLLECTION_POSTS)
                .get()
                .await()
            snapshot.toObjects(Post::class.java)
        }
    }
}