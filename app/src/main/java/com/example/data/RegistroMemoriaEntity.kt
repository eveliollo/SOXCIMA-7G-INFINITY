package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soxcima_memoria_registros")
data class RegistroMemoriaEntity(
    @PrimaryKey val id: String,
    val tipo: String, // NOTA, CLAVE, RED, CONFIG, QR, VALIDACION
    val contenidoCifradoHex: String,
    val fechaCreacion: Long,
    val firmaIntegridad: String,
    val modificable: Boolean = false
)
