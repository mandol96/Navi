package com.cho.navi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.databinding.FragmentMapBinding
import com.cho.navi.ui.addspot.AddSpotViewModel
import com.cho.navi.util.AuthManager
import com.cho.navi.util.DialogUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var map: KakaoMap? = null
    private val viewModel: AddSpotViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

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

                    moveToCurrentLocation()

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
            if (AuthManager.isLoggedIn()) {
                findNavController().navigate(R.id.action_map_to_my_page)
            } else {
                DialogUtil.showLoginRequiredDialog(requireContext()) {
                    val action = MapFragmentDirections.actionGlobalLogin()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                val latLng = LatLng.from(lat, lng)

                // ✅ 카메라 이동
                map?.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))

                // ✅ 내 위치 마커 추가
                addMarker(lat, lng)
            }
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