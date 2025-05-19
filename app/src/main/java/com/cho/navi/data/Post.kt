package com.cho.navi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String,
    val imageUrl: List<String>,
    val title: String,
    val description: String,
    val location: String,
) : Parcelable
