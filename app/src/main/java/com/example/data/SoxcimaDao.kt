package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoxcimaDao {
    @Query("SELECT * FROM soxcima_notes ORDER BY id DESC")
    fun getNotesFlow(): Flow<List<NotaSoxcimaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotaSoxcimaEntity)

    @Query("DELETE FROM soxcima_notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)

    @Query("SELECT * FROM soxcima_config WHERE id = 'config_singleton' LIMIT 1")
    fun getConfigFlow(): Flow<SoxcimaConfigEntity?>

    @Query("SELECT * FROM soxcima_config WHERE id = 'config_singleton' LIMIT 1")
    suspend fun getConfigDirect(): SoxcimaConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: SoxcimaConfigEntity)

    // === MEMORIA PERMANENTE SOXCIMA ===
    @Query("SELECT * FROM soxcima_memoria_registros ORDER BY id DESC")
    fun getMemoriaRegistrosFlow(): Flow<List<RegistroMemoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMemoriaRegistro(registro: RegistroMemoriaEntity)

    @Query("SELECT * FROM soxcima_memoria_registros WHERE id = :id LIMIT 1")
    suspend fun getMemoriaRegistroById(id: String): RegistroMemoriaEntity?

    @Query("SELECT COUNT(*) FROM soxcima_memoria_registros")
    suspend fun getMemoriaRegistrosCount(): Int
}
