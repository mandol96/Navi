package com.cho.navi.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val db: FirebaseFirestore
) {

    suspend fun addPost(post: Post): Result<Post> {
        return runCatching {
            val postData = hashMapOf(
                "category" to post.category,
                "title" to post.title,
                "description" to post.description,
                "createdAt" to Timestamp.now()
            )

            val postRef =
                db.collection("posts")
                    .add(postData)
                    .await()

            post.copy(id = postRef.id)
        }
    }
}