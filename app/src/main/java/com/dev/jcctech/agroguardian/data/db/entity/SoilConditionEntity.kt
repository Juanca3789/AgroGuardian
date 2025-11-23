package com.dev.jcctech.agroguardian.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soil_conditions")
data class SoilConditionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,                // Fecha de registro
    val type: String,              // Tipo de suelo: arcilloso, arenoso, franco
    val ph: Double?,               // pH opcional
    val organicMatter: Double?,    // % materia orgánica
    val drainage: String?          // Bueno, medio, malo
)