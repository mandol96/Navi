package com.cho.navi.ui.addspot

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cho.navi.data.Spot
import com.cho.navi.databinding.FragmentAddSpotBinding
import com.cho.navi.util.ResultKeys
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddSpotFragment : Fragment() {

    private var _binding: FragmentAddSpotBinding? = null
    private val binding get() = _binding!!

    private var isValidSpotCategory = false
    private var isValidSpotName = false
    private var isValidSpotDescription = false

    private val selectedImageUris = mutableListOf<Uri>()

    private val pickMultiplePhotos =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                selectedImageUris.addAll(uris)
                binding.ibUploadImage.setImageURI(uris.first())
            } else {
                Toast.makeText(requireContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSpotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    private fun setLayout() {
        setDropDownMenu()
        setTextField()
        binding.toolbarAddSpot.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvSpotOpenMap.setOnClickListener {
            val action = AddSpotFragmentDirections.actionAddSpotToSelectSpot()
            findNavController().navigate(action)
        }

        binding.ibUploadImage.setOnClickListener {
            pickMultiplePhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        parentFragmentManager.setFragmentResultListener(
            ResultKeys.SELECT_SPOT_RESULT,
            viewLifecycleOwner
        ) { _, bundle ->
            val address = bundle.getString(ResultKeys.SELECTED_ADDRESS)
            if (address.isNullOrBlank()) {
                binding.tvSpotOpenMap.visibility = View.VISIBLE
                binding.tvSpotAddress.visibility = View.GONE
            } else {
                binding.tvSpotAddress.text = address
                binding.tvSpotOpenMap.visibility = View.INVISIBLE
                binding.tvSpotAddress.visibility = View.VISIBLE
            }
        }

        binding.btnConfirm.setOnClickListener {
            val spot = Spot(
                address = binding.tvSpotAddress.text.toString(),
                name = binding.etSpotName.text.toString(),
                description = binding.etSpotDescription.text.toString(),
                imageUrls = null,
            )

            Firebase.firestore.collection("spots")
                .add(spot)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "장소 저장 완료", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun setDropDownMenu() {
        binding.autoCompleteTvSpotCategory.doAfterTextChanged {
            val spotCategory = it.toString()
            isValidSpotCategory = spotCategory.isNotBlank()
            updateButtonEnabledState()
        }
    }

    private fun setTextField() {
        binding.etSpotName.doAfterTextChanged {
            val spotName = it?.toString() ?: ""
            isValidSpotName = spotName.isNotBlank()
            updateButtonEnabledState()
        }
        binding.etSpotDescription.doAfterTextChanged {
            val spotDescription = it?.toString() ?: ""
            isValidSpotDescription = spotDescription.isNotBlank()
            updateButtonEnabledState()
        }
    }

    private fun updateButtonEnabledState() {
        binding.btnConfirm.isEnabled =
            isValidSpotName && isValidSpotDescription && isValidSpotCategory
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}