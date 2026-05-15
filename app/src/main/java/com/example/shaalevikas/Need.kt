package com.example.shaalevikas

import com.google.firebase.Timestamp

data class School(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String? = null
)

data class Need(
    val id: String = "",
    val schoolId: String = "",
    val title: String = "",
    val description: String = "",
    val costEstimate: Double = 0.0,
    val collectedAmount: Double = 0.0,
    val status: String = "open", // "open", "in-progress", "completed"
    val imageUrlBefore: String? = null,
    val imageUrlAfter: String? = null,
    val priority: Int = 3,
    val createdAt: Timestamp? = null
)

data class Donor(
    val id: String = "",
    val name: String = "",
    val totalPledges: Double = 0.0,
    val alumniYear: String? = null,
    val avatarUrl: String? = null
)