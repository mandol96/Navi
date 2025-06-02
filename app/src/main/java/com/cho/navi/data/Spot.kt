package com.cho.navi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Spot(
    val address: String,
    val imageUrls: List<String>?,
    val name: String,
    val description: String,
) : Parcelable
