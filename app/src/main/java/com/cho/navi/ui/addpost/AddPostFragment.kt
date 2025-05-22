package com.cho.navi.ui.addpost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cho.navi.databinding.FragmentAddPostBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var isValidPostCategory = false
    private var isValidPostTitle = false
    private var isValidPostDescription = false

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    private fun setLayout() {
        setDropDownMenu()
        setTextField()
        binding.toolbarAddPost.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnConfirm.setOnClickListener {
            val postCategory = binding.autoCompleteTvAddPostCategory.text.toString()
            val postTitle = binding.etPostTitle.text.toString()
            val postDescription = binding.etPostDescription.text.toString()

            val postData = hashMapOf(
                "category" to postCategory,
                "title" to postTitle,
                "description" to postDescription,
                "createdAt" to Timestamp.now()
            )

            db.collection("posts")
                .add(postData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "게시글이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener {e ->
                    Toast.makeText(requireContext(), "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setDropDownMenu() {
        binding.autoCompleteTvAddPostCategory.doAfterTextChanged {
            val postCategory = it.toString()
            isValidPostCategory = postCategory.isNotBlank()
            updateButtonEnabledState()
        }
    }

    private fun setTextField() {
        with(binding) {
            etPostTitle.doAfterTextChanged {
                val postTitle = it?.toString() ?: ""
                isValidPostTitle = postTitle.isNotBlank()
                updateButtonEnabledState()
            }
            etPostDescription.doAfterTextChanged {
                val postDescription = it?.toString() ?: ""
                isValidPostDescription = postDescription.isNotBlank()
                updateButtonEnabledState()
            }
        }
    }

    private fun updateButtonEnabledState() {
        binding.btnConfirm.isEnabled =
            isValidPostCategory && isValidPostTitle && isValidPostDescription
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}