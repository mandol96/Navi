package com.cho.navi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Spot(
    val id: String = "",
    val address: String = "",
    val imageUrls: List<String> = emptyList(),
    val name: String = "",
    val description: String = "",
) : Parcelable
