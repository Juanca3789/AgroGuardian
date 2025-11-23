package com.dev.jcctech.agroguardian.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crops")
data class CropEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,              // Familia: cereal, leguminosa, tubérculo, etc.
    val startDate: Long,
    val endDate: Long?
)