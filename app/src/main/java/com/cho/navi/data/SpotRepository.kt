package com.cho.navi.data

import com.cho.navi.data.source.remote.NaviService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotRepository(
    private val naviService: NaviService
) {

    fun fetchSpots(
        onCoordinate: (Double, Double) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("spots")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result.documents) {
                    val address = doc.getString("address") ?: continue

                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            val response = naviService.getCoordinatesFromAddress(address)
                            val coord = response.documents.firstOrNull()?.address

                            if (coord != null) {
                                val lat = coord.y.toDouble()
                                val lng = coord.x.toDouble()
                                onCoordinate(lat, lng)
                            }
                        }.onFailure {

                        }
                    }
                }
            }
            .addOnFailureListener {

            }
    }
}