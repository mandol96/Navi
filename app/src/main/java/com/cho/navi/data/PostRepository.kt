package com.cho.navi.data

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

class PostRepository {

    private val db = Firebase.firestore

    fun addPost(post: Post) {
        val postData = hashMapOf(
            "category" to post.category,
            "title" to post.title,
            "description" to post.description,
            "createdAt" to Timestamp.now()
        )

        db.collection("posts")
            .add(postData)
    }
}