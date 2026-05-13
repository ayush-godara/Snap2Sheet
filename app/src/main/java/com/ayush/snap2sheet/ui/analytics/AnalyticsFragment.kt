package com.ayush.snap2sheet.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ayush.snap2sheet.databinding.FragmentAnalyticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.ayush.snap2sheet.utils.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyticsViewModel by viewModels {
        ViewModelFactory((requireActivity().application as com.ayush.snap2sheet.Snap2SheetApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCharts()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.totalExpense.collect { total ->
                        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                        binding.tvTotalExpense.text = format.format(total ?: 0.0)
                    }
                }
                launch {
                    viewModel.categoryTotals.collect { totals ->
                        val entries = totals.map { PieEntry(it.total.toFloat(), it.category) }
                        val dataSet = PieDataSet(entries, "Categories").apply {
                            colors = ColorTemplate.MATERIAL_COLORS.toList()
                            valueTextSize = 14f
                            valueTextColor = Color.WHITE
                        }
                        binding.pieChart.data = PieData(dataSet)
                        binding.pieChart.invalidate()
                    }
                }
                launch {
                    viewModel.monthlyTotals.collect { totals ->
                        val entries = totals.mapIndexed { index, total ->
                            BarEntry(index.toFloat(), total.total.toFloat())
                        }
                        val labels = totals.map { it.month }
                        
                        val dataSet = BarDataSet(entries, "Monthly Expenses").apply {
                            colors = ColorTemplate.VORDIPLOM_COLORS.toList()
                            valueTextSize = 12f
                        }
                        binding.barChart.data = BarData(dataSet)
                        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        binding.barChart.invalidate()
                    }
                }
            }
        }
    }

    private fun setupCharts() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setEntryLabelColor(Color.BLACK)
            legend.isEnabled = false
        }

        binding.barChart.apply {
            description.isEnabled = false
            setFitBars(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            axisRight.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
