package com.cho.navi.data

import android.net.Uri
import com.cho.navi.data.model.Address
import com.cho.navi.data.source.remote.NaviService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class SpotRepository @Inject constructor(
    private val naviService: NaviService,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
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

    suspend fun addSpot(
        address: String,
        name: String,
        description: String,
        selectedImageUris: List<Uri>
    ): Result<Unit> {
        val imageUrls = mutableListOf<String>()
        val storageRef = storage.reference
        val spotId = UUID.randomUUID().toString()

        return kotlin.runCatching {
            selectedImageUris.forEachIndexed { index, uri ->
                val imageRef = storageRef.child("spots/$spotId/image_$index.jpg")
                imageRef.putFile(uri).await()
                val downloadUrl = imageRef.downloadUrl.await().toString()
                imageUrls.add(downloadUrl)
            }

            val spot = Spot(
                id = spotId,
                address = address,
                imageUrls = imageUrls,
                name = name,
                description = description
            )

            db.collection("spots")
                .document(spotId)
                .set(spot)
                .await()
        }
    }
}