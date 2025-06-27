package com.cho.navi.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String = "",
    val category: String = "",
    val imageUrls: List<String> = emptyList(),
    val title: String = "",
    val description: String = "",
    val location: String? = null,
    val createdAt: Timestamp? = null,
    val likeCount: Int = 0
) : Parcelable
