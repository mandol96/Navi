package com.cho.navi.data.source.remote

import com.cho.navi.BuildConfig
import com.cho.navi.data.model.AddressResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaviService {

    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
    @GET("v2/local/geo/coord2address.json")
    suspend fun getAddressFromCoordinates(
        @Query("x") longitude: Double,
        @Query("y") latitude: Double
    ): AddressResponse

    companion object {
        fun create(): NaviService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(NaviService::class.java)
        }
    }
}