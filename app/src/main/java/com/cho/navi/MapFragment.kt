package com.cho.navi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.data.SpotRepository
import com.cho.navi.data.source.remote.NaviService
import com.cho.navi.databinding.FragmentMapBinding
import com.cho.navi.ui.addspot.AddSpotViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    private val viewModel: AddSpotViewModel by viewModels {
        AddSpotViewModel.provideFactory(
            SpotRepository(
                NaviService.create(),
                Firebase.firestore,
                Firebase.storage
            )
        )
    }

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
                    viewModel.loadSpots()
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.coordinate.collect { coordinateList ->
                            coordinateList.forEach { (lat, lng) ->
                                addMarker(lat, lng)
                            }
                        }
                    }
                }
            }
        )

        binding.fabAddSpot.setOnClickListener {
            findNavController().navigate(R.id.action_map_to_addSpot)
        }

        binding.ibMyPage.setOnClickListener {
            findNavController().navigate(R.id.action_map_to_my_page)
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