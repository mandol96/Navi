package com.cho.navi.ui.addspot

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.R
import com.cho.navi.data.SpotRepository
import com.cho.navi.data.source.remote.NaviService
import com.cho.navi.databinding.FragmentAddSpotBinding
import com.cho.navi.util.ResultKeys
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch

class AddSpotFragment : Fragment() {

    private var _binding: FragmentAddSpotBinding? = null
    private val binding get() = _binding!!

    private var isValidSpotCategory = false
    private var isValidSpotName = false
    private var isValidSpotDescription = false
    private var isValidSpotAddress = false
    private var isValidSpotImages = false

    private val selectedImageUris = mutableListOf<Uri>()

    private val viewModel: AddSpotViewModel by viewModels {
        AddSpotViewModel.provideFactory(
            SpotRepository(
                NaviService.create(),
                Firebase.firestore,
                Firebase.storage
            )
        )
    }

    private val pickMultiplePhotos =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                selectedImageUris.addAll(uris)
                binding.ibUploadImage.setImageURI(uris.first())
                isValidSpotImages = true
                updateButtonEnabledState()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_unselected_message), Toast.LENGTH_SHORT
                ).show()
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

                isValidSpotAddress = false
                updateButtonEnabledState()
            } else {
                binding.tvSpotAddress.text = address
                binding.tvSpotOpenMap.visibility = View.INVISIBLE
                binding.tvSpotAddress.visibility = View.VISIBLE

                isValidSpotAddress = true
                updateButtonEnabledState()
            }
        }

        binding.btnConfirm.setOnClickListener {
            val address = binding.tvSpotAddress.text.toString()
            val name = binding.etSpotName.text.toString()
            val description = binding.etSpotDescription.text.toString()

            viewModel.addSpot(address, name, description, selectedImageUris)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        AddSpotUiState.Idle -> Unit
                        AddSpotUiState.Loading -> showProgress()
                        AddSpotUiState.Success -> completeTask()
                        is AddSpotUiState.Error -> showError()
                    }
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
            isValidSpotName && isValidSpotDescription && isValidSpotCategory && isValidSpotAddress && isValidSpotImages
    }

    private fun showProgress() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressCircular.visibility = View.GONE
    }

    private fun completeTask() {
        hideProgress()
        Toast.makeText(
            requireContext(),
            getString(R.string.toast_save_post_message), Toast.LENGTH_SHORT
        ).show()
        findNavController().navigateUp()
    }

    private fun showError() {
        hideProgress()
        Toast.makeText(
            requireContext(),
            getString(R.string.toast_error_post_message), Toast.LENGTH_SHORT
        )
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}