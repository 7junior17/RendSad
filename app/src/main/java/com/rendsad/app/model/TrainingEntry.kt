package com.rendsad.app.model

enum class TrainingBadge { COMPLETED, MODERATE, PLAIN }

data class TrainingEntry(
    val dateText: String,
    val badgeText: String,
    val badge: TrainingBadge,
    val exercises: List<String>,
    val rpe: Int?,
    val note: String?,
    val noteIsGood: Boolean
)