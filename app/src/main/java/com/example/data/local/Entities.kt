package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_opportunities")
data class JobOpportunity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Central", "UP", "Banking", "Railway"
    val department: String = "",
    val vacancies: String = "",
    val dateText: String = "",
    val statusTag: String = "", // "NEW", "EXTENDED", "UPCOMING"
    val logoUrl: String = "",
    val description: String = "",
    val applyUrl: String = "",
    val isSaved: Boolean = false
)

@Entity(tableName = "admit_cards")
data class AdmitCardItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examTitle: String,
    val department: String,
    val tag: String, // "CBT II", "Medical", "Tier II", "Bank"
    val statusBadge: String, // "New", "Live Now", "Active Link"
    val examDateOrCenter: String,
    val isSaved: Boolean = false,
    val downloadUrl: String = ""
)

@Entity(tableName = "exam_results")
data class ExamResultItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examTitle: String,
    val department: String,
    val tag: String, // "Merit List", "Final Result", "Score Card"
    val releasedDate: String,
    val isNew: Boolean = false,
    val isSaved: Boolean = false,
    val resultUrl: String = ""
)

@Entity(tableName = "user_applications")
data class UserApplication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val registrationNo: String,
    val appliedDate: String,
    val status: String
)
