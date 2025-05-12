package com.cho.navi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cho.navi.databinding.FragmentMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import java.lang.Exception

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

				override fun onMapError(p0: Exception?) {
					p0?.printStackTrace()
				}
			}, object : KakaoMapReadyCallback() {
				override fun onMapReady(p0: KakaoMap) {
					map = p0
				}
			}
		)
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