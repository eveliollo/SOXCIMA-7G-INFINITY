package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soxcima_notes")
data class NotaSoxcimaEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val contenido: String,
    val fecha: String,
    val cifrada: Boolean,
    val cifradoPayload: String? = null
)
