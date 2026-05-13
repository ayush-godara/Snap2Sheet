package com.ayush.snap2sheet.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ayush.snap2sheet.data.GroupExpense
import com.ayush.snap2sheet.databinding.ItemGroupExpenseBinding
import com.ayush.snap2sheet.utils.DateUtils
import java.text.NumberFormat
import java.util.Locale

class GroupExpenseAdapter : ListAdapter<GroupExpense, GroupExpenseAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemGroupExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: GroupExpense) {
            binding.tvMerchant.text = expense.merchantName
            binding.tvCategory.text = expense.category
            binding.tvDate.text = DateUtils.formatForDisplay(expense.date)
            binding.tvAddedBy.text = "Added by ${expense.addedByName}"
            
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            binding.tvAmount.text = format.format(expense.amount)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GroupExpense>() {
        override fun areItemsTheSame(oldItem: GroupExpense, newItem: GroupExpense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GroupExpense, newItem: GroupExpense) = oldItem == newItem
    }
}
