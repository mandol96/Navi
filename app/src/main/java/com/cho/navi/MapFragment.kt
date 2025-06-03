package com.cho.navi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cho.navi.databinding.FragmentMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var map: KakaoMap? = null

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

                    val cameraPosition = CameraPosition.from(
                        37.3990995274297, 127.107516796647, 8, 0.0, 0.0, 0.0
                    )
                    val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                    kakaoMap.moveCamera(cameraUpdate)

                    val iconStyle = LabelStyle.from(R.drawable.ic_marker_2)

                    val styles = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(iconStyle))

                    val labelOptions = LabelOptions.from(
                        LatLng.from(37.3990995274297, 127.107516796647)
                    ).setStyles(styles)

                    kakaoMap.labelManager?.layer?.addLabel(labelOptions)

                }
            }
        )

        binding.fabAddSpot.setOnClickListener {
            findNavController().navigate(R.id.action_map_to_addSpot)
        }
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