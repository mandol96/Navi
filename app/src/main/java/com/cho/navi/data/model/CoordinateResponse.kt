package com.cho.navi.data.model

import com.squareup.moshi.Json
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
    @Json(name = "address_name") val addressName: String?,
    val x: String, // 경도
    val y: String  // 위도
)