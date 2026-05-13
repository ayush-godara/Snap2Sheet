package com.ayush.snap2sheet.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayush.snap2sheet.R
import com.ayush.snap2sheet.databinding.FragmentGroupDetailBinding
import com.ayush.snap2sheet.utils.GroupViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupViewModel by activityViewModels {
        GroupViewModelFactory((requireActivity().application as com.ayush.snap2sheet.Snap2SheetApp).firestoreRepository)
    }

    private lateinit var adapter: GroupExpenseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupExpenseAdapter()
        binding.recyclerExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerExpenses.adapter = adapter

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_groupDetailFragment_to_addGroupExpenseFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedGroup.collect { group ->
                        if (group != null) {
                            binding.tvGroupName.text = group.name
                            binding.tvInviteCode.text = "Invite Code: ${group.inviteCode}"
                            binding.tvMembers.text = "${group.members.size} members"
                            
                            // Show member names
                            val memberList = group.memberNames.values.joinToString(", ")
                            binding.tvMemberList.text = memberList
                        }
                    }
                }
                launch {
                    viewModel.groupExpenses.collect { expenses ->
                        adapter.submitList(expenses)
                        binding.tvEmptyExpenses.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE

                        // Per-member breakdown
                        val byMember = expenses.groupBy { it.addedByName }
                        val breakdown = byMember.map { (name, items) ->
                            val total = items.sumOf { it.amount }
                            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                            "$name: ${format.format(total)}"
                        }.joinToString("\n")
                        binding.tvBreakdown.text = if (breakdown.isNotBlank()) breakdown else "No expenses yet"
                    }
                }
                launch {
                    viewModel.groupTotal.collect { total ->
                        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                        binding.tvGroupTotal.text = format.format(total)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
