package com.cho.navi.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoordinateResponse(
    val documents: List<CoordinateDocument>
)

@JsonClass(generateAdapter = true)
data class CoordinateDocument(
    val address: CoordinateAddress?
)

@JsonClass(generateAdapter = true)
data class CoordinateAddress(
    val x: String,
    val y: String
)
