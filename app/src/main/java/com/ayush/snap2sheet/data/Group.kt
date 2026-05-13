package com.ayush.snap2sheet.data

data class Group(
    val id: String = "",
    val name: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val members: List<String> = emptyList(),
    val memberNames: Map<String, String> = emptyMap(),
    val inviteCode: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
