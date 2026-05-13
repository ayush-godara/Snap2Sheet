package com.ayush.snap2sheet.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.snap2sheet.data.FirestoreRepository
import com.ayush.snap2sheet.data.Group
import com.ayush.snap2sheet.data.GroupExpense
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupViewModel(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    private val userName: String
        get() = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown"

    // Groups list
    val myGroups: StateFlow<List<Group>> = firestoreRepository.getMyGroups(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Selected group expenses
    private val _selectedGroupId = MutableStateFlow("")
    
    val groupExpenses: StateFlow<List<GroupExpense>> = _selectedGroupId
        .flatMapLatest { groupId ->
            if (groupId.isBlank()) flowOf(emptyList())
            else firestoreRepository.getGroupExpenses(groupId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val groupTotal: StateFlow<Double> = _selectedGroupId
        .flatMapLatest { groupId ->
            if (groupId.isBlank()) flowOf(0.0)
            else firestoreRepository.getGroupTotal(groupId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    // Selected group info
    private val _selectedGroup = MutableStateFlow<Group?>(null)
    val selectedGroup: StateFlow<Group?> = _selectedGroup

    // Status events
    private val _createStatus = MutableSharedFlow<Result<String>>()
    val createStatus: SharedFlow<Result<String>> = _createStatus

    private val _joinStatus = MutableSharedFlow<Result<Group?>>()
    val joinStatus: SharedFlow<Result<Group?>> = _joinStatus

    private val _addExpenseStatus = MutableSharedFlow<Result<String>>()
    val addExpenseStatus: SharedFlow<Result<String>> = _addExpenseStatus

    fun selectGroup(groupId: String) {
        _selectedGroupId.value = groupId
        viewModelScope.launch {
            val group = firestoreRepository.getGroup(groupId)
            _selectedGroup.value = group
        }
    }

    fun createGroup(name: String) {
        viewModelScope.launch {
            try {
                val groupId = firestoreRepository.createGroup(name, userId, userName)
                _createStatus.emit(Result.success(groupId))
            } catch (e: Exception) {
                _createStatus.emit(Result.failure(e))
            }
        }
    }

    fun joinGroup(inviteCode: String) {
        viewModelScope.launch {
            try {
                val group = firestoreRepository.joinGroup(inviteCode.uppercase(), userId, userName)
                _joinStatus.emit(Result.success(group))
            } catch (e: Exception) {
                _joinStatus.emit(Result.failure(e))
            }
        }
    }

    fun addGroupExpense(groupId: String, expense: GroupExpense) {
        viewModelScope.launch {
            try {
                val fullExpense = expense.copy(
                    groupId = groupId,
                    addedBy = userId,
                    addedByName = userName
                )
                firestoreRepository.addGroupExpense(fullExpense)
                _addExpenseStatus.emit(Result.success("Expense added!"))
            } catch (e: Exception) {
                _addExpenseStatus.emit(Result.failure(e))
            }
        }
    }

    fun deleteGroupExpense(expenseId: String) {
        viewModelScope.launch {
            firestoreRepository.deleteGroupExpense(expenseId)
        }
    }
}
