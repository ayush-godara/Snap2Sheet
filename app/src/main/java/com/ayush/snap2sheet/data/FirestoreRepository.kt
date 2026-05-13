package com.ayush.snap2sheet.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val groupsCollection = db.collection("groups")
    private val groupExpensesCollection = db.collection("group_expenses")

    // ── Group Operations ──

    suspend fun createGroup(name: String, userId: String, userName: String): String {
        val inviteCode = generateInviteCode()
        val docRef = groupsCollection.document()
        val group = hashMapOf(
            "id" to docRef.id,
            "name" to name,
            "createdBy" to userId,
            "createdByName" to userName,
            "members" to listOf(userId),
            "memberNames" to mapOf(userId to userName),
            "inviteCode" to inviteCode,
            "createdAt" to System.currentTimeMillis()
        )
        docRef.set(group).await()
        return docRef.id
    }

    suspend fun joinGroup(inviteCode: String, userId: String, userName: String): Group? {
        val snapshot = groupsCollection
            .whereEqualTo("inviteCode", inviteCode)
            .get()
            .await()

        if (snapshot.isEmpty) return null

        val doc = snapshot.documents[0]
        val currentMembers = doc.get("members") as? List<String> ?: emptyList()
        
        if (currentMembers.contains(userId)) {
            return doc.toObject(Group::class.java)
        }

        val updatedMembers = currentMembers + userId
        @Suppress("UNCHECKED_CAST")
        val currentNames = doc.get("memberNames") as? Map<String, String> ?: emptyMap()
        val updatedNames = currentNames + (userId to userName)

        doc.reference.update(
            mapOf(
                "members" to updatedMembers,
                "memberNames" to updatedNames
            )
        ).await()

        return doc.toObject(Group::class.java)?.copy(
            members = updatedMembers,
            memberNames = updatedNames
        )
    }

    fun getMyGroups(userId: String): Flow<List<Group>> = callbackFlow {
        val listener = groupsCollection
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val groups = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Group::class.java)
                } ?: emptyList()
                trySend(groups)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getGroup(groupId: String): Group? {
        val doc = groupsCollection.document(groupId).get().await()
        return doc.toObject(Group::class.java)
    }

    suspend fun deleteGroup(groupId: String) {
        groupsCollection.document(groupId).delete().await()
        // Also delete all expenses in the group
        val expenses = groupExpensesCollection
            .whereEqualTo("groupId", groupId)
            .get()
            .await()
        for (doc in expenses.documents) {
            doc.reference.delete()
        }
    }

    // ── Group Expense Operations ──

    suspend fun addGroupExpense(expense: GroupExpense): String {
        val docRef = groupExpensesCollection.document()
        val data = hashMapOf(
            "id" to docRef.id,
            "groupId" to expense.groupId,
            "addedBy" to expense.addedBy,
            "addedByName" to expense.addedByName,
            "merchantName" to expense.merchantName,
            "amount" to expense.amount,
            "category" to expense.category,
            "date" to expense.date,
            "imagePath" to expense.imagePath,
            "notes" to expense.notes,
            "timestamp" to System.currentTimeMillis()
        )
        docRef.set(data).await()
        return docRef.id
    }

    fun getGroupExpenses(groupId: String): Flow<List<GroupExpense>> = callbackFlow {
        val listener = groupExpensesCollection
            .whereEqualTo("groupId", groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(GroupExpense::class.java)
                }?.sortedByDescending { it.timestamp } ?: emptyList()
                trySend(expenses)
            }
        awaitClose { listener.remove() }
    }

    suspend fun deleteGroupExpense(expenseId: String) {
        groupExpensesCollection.document(expenseId).delete().await()
    }

    fun getGroupTotal(groupId: String): Flow<Double> = callbackFlow {
        val listener = groupExpensesCollection
            .whereEqualTo("groupId", groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0.0)
                    return@addSnapshotListener
                }
                val total = snapshot?.documents?.sumOf { doc ->
                    doc.getDouble("amount") ?: 0.0
                } ?: 0.0
                trySend(total)
            }
        awaitClose { listener.remove() }
    }

    // ── Helpers ──

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}
