package com.dev.jcctech.agroguardian.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parcel_state")
data class ParcelStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val practice: String,           // Práctica aplicada (riego, fertilización, abono verde, barbecho)
    val result: String?,            // Resultado observado (ej. "suelo más húmedo", "rendimiento bajo")
    val diagnosis: String?,         // Diagnóstico (plaga, enfermedad, condición del suelo)
    val timestamp: Long             // Momento de registro
)