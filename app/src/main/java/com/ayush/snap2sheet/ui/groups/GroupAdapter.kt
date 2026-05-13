package com.ayush.snap2sheet.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ayush.snap2sheet.data.Group
import com.ayush.snap2sheet.databinding.ItemGroupBinding

class GroupAdapter(
    private val onGroupClick: (Group) -> Unit
) : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.tvGroupName.text = group.name
            binding.tvMemberCount.text = "${group.members.size} members"
            binding.tvInviteCode.text = "Code: ${group.inviteCode}"
            binding.tvCreatedBy.text = "by ${group.createdByName}"
            binding.root.setOnClickListener { onGroupClick(group) }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Group, newItem: Group) = oldItem == newItem
    }
}
