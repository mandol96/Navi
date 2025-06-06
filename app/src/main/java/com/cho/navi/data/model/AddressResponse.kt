package com.cho.navi.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddressResponse(
    val documents: List<Document>
)

@JsonClass(generateAdapter = true)
data class Document(
    @Json(name = "road_address") val roadAddress: RoadAddress?,
    @Json(name = "address") val address: Address?
)

@JsonClass(generateAdapter = true)
data class RoadAddress(
    @Json(name = "address_name") val addressName: String,
    @Json(name = "building_name") val buildingName: String?
)

@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "address_name") val addressName: String?
)
