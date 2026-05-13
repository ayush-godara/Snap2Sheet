package com.ayush.snap2sheet.ui.scan

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ayush.snap2sheet.data.Expense
import com.ayush.snap2sheet.databinding.FragmentScanBinding
import com.ayush.snap2sheet.utils.ViewModelFactory
import java.io.File

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScanViewModel by viewModels {
        ViewModelFactory((requireActivity().application as com.ayush.snap2sheet.Snap2SheetApp).repository)
    }
    private var imageUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null) {
            viewModel.processImage(requireContext(), imageUri!!)
        } else {
            Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCapture.setOnClickListener {
            val photoFile = File(requireContext().cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
            imageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", photoFile)
            takePicture.launch(imageUri)
        }

        binding.btnSave.setOnClickListener {
            val merchant = binding.etMerchant.text.toString()
            val amountStr = binding.etAmount.text.toString()
            val date = binding.etDate.text.toString()
            val category = binding.spinnerCategory.selectedItem.toString()

            if (merchant.isBlank() || amountStr.isBlank() || date.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val expense = Expense(
                merchantName = merchant,
                amount = amount,
                date = date,
                category = category,
                imagePath = imageUri?.toString() ?: ""
            )
            viewModel.saveExpense(expense)
        }

        viewModel.receiptData.observe(viewLifecycleOwner) { data ->
            binding.etMerchant.setText(data.merchantName)
            binding.etAmount.setText(data.amount.toString())
            binding.etDate.setText(data.date)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCapture.isEnabled = !isLoading
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Expense Saved!", Toast.LENGTH_SHORT).show()
                binding.etMerchant.text?.clear()
                binding.etAmount.text?.clear()
                binding.etDate.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
