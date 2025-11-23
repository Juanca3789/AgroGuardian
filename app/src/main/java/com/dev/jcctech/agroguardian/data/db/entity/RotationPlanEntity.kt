package com.dev.jcctech.agroguardian.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rotation_plan")
data class RotationPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val generatedAt: Long,          // Fecha de cálculo
    val consideredCrops: List<Int>, // IDs de los cultivos anteriores usados en el cálculo
    val recommendedSequence: List<String>, // Secuencia de cultivos sugeridos
    val rationale: String           // Explicación del resultado
)