package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soxcima_config")
data class SoxcimaConfigEntity(
    @PrimaryKey val id: String = "config_singleton",
    val nodeIdentity: String = "SOCXIMA-NODO-UNICO",
    val bridgeActive: Boolean = false,
    val destinationAllowed: String? = null,
    val localEncryptionKeyHex: String = "00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff", // Simulated 32-byte hex key
    val accesoMundialAbierto: Boolean = false
)
