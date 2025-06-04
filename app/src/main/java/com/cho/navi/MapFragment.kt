package com.cho.navi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.data.source.remote.NaviService
import com.cho.navi.databinding.FragmentMapBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var map: KakaoMap? = null
    private lateinit var service: NaviService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        service = NaviService.create()

        val mapView = binding.mapView
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {

                }

                override fun onMapError(error: Exception?) {
                    error?.printStackTrace()
                }
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    map = kakaoMap
                    fetchSpotsAndAddMarkers()
                }
            }
        )

        binding.fabAddSpot.setOnClickListener {
            findNavController().navigate(R.id.action_map_to_addSpot)
        }
    }

    private fun fetchSpotsAndAddMarkers() {
        val db = FirebaseFirestore.getInstance()
        db.collection("spots")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result.documents) {
                    val address = doc.getString("address") ?: continue

                    lifecycleScope.launch {
                        runCatching {
                            val response = service.getCoordinatesFromAddress(address)
                            val coord = response.documents.firstOrNull()?.address

                            if (coord != null) {
                                val lat = coord.y.toDouble()
                                val lng = coord.x.toDouble()
                                addMarker(lat, lng)
                            }
                        }.onFailure {

                        }
                    }
                }
            }
            .addOnFailureListener {

            }
    }

    private fun addMarker(lat: Double, lng: Double) {
        val labelStyle = LabelStyle.from(R.drawable.ic_marker_2)
        val styles = map?.labelManager?.addLabelStyles(LabelStyles.from(labelStyle))

        val latLng = LatLng.from(lat, lng)
        val options = LabelOptions.from(latLng)
            .setStyles(styles)

        map?.labelManager?.layer?.addLabel(options)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}