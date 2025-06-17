package com.cho.navi.ui.addspot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.R
import com.cho.navi.data.SpotRepository
import com.cho.navi.data.source.remote.NaviService
import com.cho.navi.databinding.FragmentSelectSpotBinding
import com.cho.navi.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import kotlinx.coroutines.launch

class SelectSpotFragment : Fragment() {

    private var _binding: FragmentSelectSpotBinding? = null
    private val binding get() = _binding!!

    private var map: KakaoMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: SelectSpotViewModel by viewModels {
        SelectSpotViewModel.provideFactory(SpotRepository(NaviService.create(), Firebase.firestore))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectSpotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
        collectUiState()
    }

    private fun setLayout() {
        setMap()

        binding.toolbarSelectSpot.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSelected.setOnClickListener {
            val address = binding.tvCurrentAddress.text.toString()
            parentFragmentManager.setFragmentResult("select_spot_result", bundleOf(
                "selected_address" to address
            ))
            findNavController().navigateUp()
        }
    }

    private fun setMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapView = binding.mapSelectSpot
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {

                }

                override fun onMapError(error: Exception?) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_error_load_map), Toast.LENGTH_SHORT
                    ).show()
                }
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    map = kakaoMap
                    currentLocation()

                    kakaoMap.setOnCameraMoveEndListener { _, cameraPosition, _ ->
                        val center = cameraPosition.position
                        viewModel.fetchAddress(center.longitude, center.latitude)
                    }
                }
            }
        )
    }

    private fun currentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.REQUEST_LOCATION_PERMISSION
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val lat = it.latitude
                val lng = it.longitude

                val latLng = LatLng.from(lat, lng)
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
                map?.moveCamera(cameraUpdate)
            }
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        SelectSpotUiState.Loading -> showProgress()
                        is SelectSpotUiState.Success -> completeTask(uiState.address)
                        is SelectSpotUiState.Error -> showError()
                    }
                }
        }
    }

    private fun showProgress() {
        binding.groupSelectSpot.visibility = View.GONE
        binding.groupSelectSpot.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.groupSelectSpot.visibility = View.VISIBLE
        binding.progressCircular.visibility = View.GONE
    }

    private fun completeTask(address: String?) {
        hideProgress()
        binding.tvCurrentAddress.text = address
    }

    private fun showError() {
        hideProgress()
        Toast.makeText(requireContext(),
            getString(R.string.toast_error_post_message), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}