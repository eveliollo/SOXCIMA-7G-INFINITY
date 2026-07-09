package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SoxcimaRepository(private val soxcimaDao: SoxcimaDao) {
    val notesFlow: Flow<List<NotaSoxcimaEntity>> = soxcimaDao.getNotesFlow()
    val configFlow: Flow<SoxcimaConfigEntity?> = soxcimaDao.getConfigFlow()

    suspend fun insertNote(note: NotaSoxcimaEntity) {
        soxcimaDao.insertNote(note)
    }

    suspend fun deleteNoteById(noteId: String) {
        soxcimaDao.deleteNoteById(noteId)
    }

    suspend fun getConfigDirect(): SoxcimaConfigEntity {
        return soxcimaDao.getConfigDirect() ?: SoxcimaConfigEntity().also {
            soxcimaDao.insertOrUpdateConfig(it)
        }
    }

    suspend fun updateConfig(config: SoxcimaConfigEntity) {
        soxcimaDao.insertOrUpdateConfig(config)
    }

    // === MEMORIA PERMANENTE SOXCIMA ===
    val memoriaRegistrosFlow: Flow<List<RegistroMemoriaEntity>> = soxcimaDao.getMemoriaRegistrosFlow()

    suspend fun insertMemoriaRegistro(registro: RegistroMemoriaEntity) {
        soxcimaDao.insertMemoriaRegistro(registro)
    }

    suspend fun getMemoriaRegistroById(id: String): RegistroMemoriaEntity? {
        return soxcimaDao.getMemoriaRegistroById(id)
    }

    suspend fun getMemoriaRegistrosCount(): Int {
        return soxcimaDao.getMemoriaRegistrosCount()
    }
}
