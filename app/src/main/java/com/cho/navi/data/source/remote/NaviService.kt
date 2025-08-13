package com.cho.navi.data.source.remote

import com.cho.navi.data.model.AddressResponse
import com.cho.navi.data.model.CoordinateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NaviService {

    @GET("v2/local/geo/coord2address.json")
    suspend fun getAddressFromCoordinates(
        @Query("x") longitude: Double,
        @Query("y") latitude: Double
    ): AddressResponse

    @GET("v2/local/search/address.json")
    suspend fun getCoordinatesFromAddress(
        @Query("query") address: String
    ): CoordinateResponse
}