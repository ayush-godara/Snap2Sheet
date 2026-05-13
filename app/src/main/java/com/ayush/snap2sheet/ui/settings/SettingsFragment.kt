package com.ayush.snap2sheet.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ayush.snap2sheet.databinding.FragmentSettingsBinding
import com.ayush.snap2sheet.utils.CsvExporter
import com.ayush.snap2sheet.utils.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        ViewModelFactory((requireActivity().application as com.ayush.snap2sheet.Snap2SheetApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClearData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to delete all expenses? This cannot be undone.")
                .setPositiveButton("Clear") { _, _ ->
                    viewModel.clearAllData()
                    Toast.makeText(requireContext(), "All data cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnExportCsv.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val expenses = viewModel.getAllExpensesForExport()
                val path = CsvExporter.exportToCsv(requireContext(), expenses)
                if (path != null) {
                    Toast.makeText(requireContext(), "Exported to: $path", Toast.LENGTH_LONG).show()
                    shareCsv(path)
                } else {
                    Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shareCsv(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Expenses Export")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share CSV"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
