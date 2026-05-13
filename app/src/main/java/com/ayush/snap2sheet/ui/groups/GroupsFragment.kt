package com.ayush.snap2sheet.ui.groups

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayush.snap2sheet.R
import com.ayush.snap2sheet.databinding.FragmentGroupsBinding
import com.ayush.snap2sheet.utils.GroupViewModelFactory
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupViewModel by activityViewModels {
        GroupViewModelFactory((requireActivity().application as com.ayush.snap2sheet.Snap2SheetApp).firestoreRepository)
    }

    private lateinit var adapter: GroupAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupAdapter { group ->
            viewModel.selectGroup(group.id)
            findNavController().navigate(R.id.action_groupsFragment_to_groupDetailFragment)
        }
        binding.recyclerGroups.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerGroups.adapter = adapter

        binding.fabCreateGroup.setOnClickListener { showCreateGroupDialog() }
        binding.btnJoinGroup.setOnClickListener { showJoinGroupDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.myGroups.collect { groups ->
                        adapter.submitList(groups)
                        binding.tvEmpty.visibility = if (groups.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.createStatus.collect { result ->
                        result.onSuccess {
                            Toast.makeText(requireContext(), "Group created!", Toast.LENGTH_SHORT).show()
                        }
                        result.onFailure { e ->
                            Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    viewModel.joinStatus.collect { result ->
                        result.onSuccess { group ->
                            if (group != null) {
                                Toast.makeText(requireContext(), "Joined ${group.name}!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Invalid invite code", Toast.LENGTH_SHORT).show()
                            }
                        }
                        result.onFailure { e ->
                            Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showCreateGroupDialog() {
        val input = EditText(requireContext()).apply {
            hint = "e.g., My Flat, Office Team"
            setPadding(48, 32, 48, 16)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Create Group")
            .setMessage("Enter a name for your group:")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotBlank()) {
                    viewModel.createGroup(name)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showJoinGroupDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter 6-digit invite code"
            setPadding(48, 32, 48, 16)
            isAllCaps = true
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Join Group")
            .setMessage("Enter the invite code shared by the group admin:")
            .setView(input)
            .setPositiveButton("Join") { _, _ ->
                val code = input.text.toString().trim()
                if (code.isNotBlank()) {
                    viewModel.joinGroup(code)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
