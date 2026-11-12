package com.rendsad.app.model

enum class StudentStatus { INJURY, OVERLOAD, OPTIMAL, POSITIVE }

data class Student(
    val name: String,
    val number: Int,
    val position: String,
    val level: String,
    val category: String,
    val status: StudentStatus,
    val statusText: String
)