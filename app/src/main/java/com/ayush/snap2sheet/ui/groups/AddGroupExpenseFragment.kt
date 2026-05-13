package com.ayush.snap2sheet.ui.groups

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ayush.snap2sheet.R
import com.ayush.snap2sheet.data.GroupExpense
import com.ayush.snap2sheet.databinding.FragmentAddGroupExpenseBinding
import com.ayush.snap2sheet.utils.GroupViewModelFactory
import com.ayush.snap2sheet.utils.OCRHelper
import kotlinx.coroutines.launch
import java.io.File

class AddGroupExpenseFragment : Fragment() {

    private var _binding: FragmentAddGroupExpenseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupViewModel by activityViewModels {
        GroupViewModelFactory((requireActivity().application as com.ayush.snap2sheet.Snap2SheetApp).firestoreRepository)
    }

    private var imageUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null) {
            processImage(imageUri!!)
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            processImage(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddGroupExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCamera.setOnClickListener {
            val photoFile = File(requireContext().cacheDir, "group_receipt_${System.currentTimeMillis()}.jpg")
            imageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", photoFile)
            takePicture.launch(imageUri)
        }

        binding.btnGallery.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSave.setOnClickListener { saveExpense() }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addExpenseStatus.collect { result ->
                    result.onSuccess {
                        Toast.makeText(requireContext(), "Expense added to group!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    result.onFailure { e ->
                        Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun processImage(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            val data = OCRHelper.processImage(requireContext(), uri)
            binding.etMerchant.setText(data.merchantName)
            binding.etAmount.setText(if (data.amount > 0) data.amount.toString() else "")
            binding.etDate.setText(data.date)

            // Auto-select category
            val categoriesArray = resources.getStringArray(R.array.categories_array)
            val idx = categoriesArray.indexOf(data.category)
            if (idx >= 0) binding.spinnerCategory.setSelection(idx)

            binding.progressBar.visibility = View.GONE
        }
    }

    private fun saveExpense() {
        val merchant = binding.etMerchant.text.toString()
        val amountStr = binding.etAmount.text.toString()
        val date = binding.etDate.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (merchant.isBlank() || amountStr.isBlank() || date.isBlank()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val groupId = viewModel.selectedGroup.value?.id ?: return

        val expense = GroupExpense(
            merchantName = merchant,
            amount = amount,
            category = category,
            date = date,
            imagePath = imageUri?.toString() ?: ""
        )
        viewModel.addGroupExpense(groupId, expense)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
