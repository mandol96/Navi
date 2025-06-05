package com.cho.navi.data.source.remote

import com.cho.navi.BuildConfig
import com.cho.navi.data.model.AddressResponse
import com.cho.navi.data.model.CoordinateResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    companion object {
        fun create(): NaviService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val headerInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
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