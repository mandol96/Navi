package com.cho.navi.data

import com.cho.navi.data.model.Address
import com.cho.navi.data.source.remote.NaviService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SpotRepository(
    private val naviService: NaviService,
    private val db: FirebaseFirestore
) {

    suspend fun fetchSpots(onCoordinate: (Double, Double) -> Unit) {
        try {
            val result = db.collection("spots").get().await()
            for (doc in result.documents) {
                val address = doc.getString("address") ?: continue
                try {
                    val response = naviService.getCoordinatesFromAddress(address)
                    val coordinate = response.documents.firstOrNull()?.address
                    if (coordinate != null) {
                        val lat = coordinate.y.toDouble()
                        val lng = coordinate.x.toDouble()
                        onCoordinate(lat, lng)
                    }
                } catch (e: Exception) {

                }
            }
        } catch (e: Exception) {

        }
    }

    suspend fun getAddressFromCoordinates(longitude: Double, latitude: Double): Address? {
        val response = naviService.getAddressFromCoordinates(longitude, latitude)
        return response.documents.firstOrNull()?.address
    }
}