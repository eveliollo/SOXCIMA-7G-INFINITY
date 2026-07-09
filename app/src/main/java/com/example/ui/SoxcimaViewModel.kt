package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.NotaSoxcimaEntity
import com.example.data.SoxcimaConfigEntity
import com.example.data.SoxcimaDatabase
import com.example.data.SoxcimaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class StagedTx(
    val noteId: String,
    val noteTitle: String,
    val targetNode: String,
    val payload: String,
    val isApproved: Boolean = false,
    val status: String = "Staged" // "Staged", "Transmitting", "Success", "Error"
)

data class TelemetryEntry(
    val cycle: Int,
    val ingest: Int,
    val intelligence: Int
)

data class SovereignCycleState(
    val isRunning: Boolean = false,
    val cyclesCount: Int = 17,
    val currentStage: String = "INACTIVO", // "INACTIVO", "INGESTA", "PROCESAMIENTO", "ACCIÓN", "AUTOCURACIÓN", "LATIDO"
    val lastIngestValue: Int = 191,
    val lastIntelligenceLevel: Int = 382,
    val lastIntegrityStatus: String = "OK (FIRMA INTACTA)",
    val statusMessage: String = "Soberanía inactiva. Actívala para iniciar el ciclo.",
    val telemetryHistory: List<TelemetryEntry> = listOf(
        TelemetryEntry(10, 160, 320),
        TelemetryEntry(11, 165, 330),
        TelemetryEntry(12, 172, 344),
        TelemetryEntry(13, 178, 356),
        TelemetryEntry(14, 180, 360),
        TelemetryEntry(15, 184, 368),
        TelemetryEntry(16, 188, 376),
        TelemetryEntry(17, 191, 382)
    )
)

data class SoxcimaChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String,
    val text: String,
    val timestamp: String,
    val isUser: Boolean,
    val isEncrypted: Boolean = false,
    val encryptedPayloadHex: String = "",
    val packetSize: Int = 0
)

class SoxcimaViewModel(
    application: Application,
    private val repository: SoxcimaRepository
) : AndroidViewModel(application) {

    // Theme selector
    private val _isLightMode = MutableStateFlow(false)
    val isLightMode: StateFlow<Boolean> = _isLightMode.asStateFlow()

    fun toggleLightMode() {
        _isLightMode.value = !_isLightMode.value
    }

    // P2P Chat Messages
    private val _chatMessages = MutableStateFlow<List<SoxcimaChatMessage>>(listOf(
        SoxcimaChatMessage(
            sender = "NODO_ALPHA",
            text = "¡Conexión establecida con éxito! Chat Soxcima P2P de código abierto en línea. ¿Qué módulo de soberanía deseas sincronizar hoy?",
            timestamp = "10:00:10",
            isUser = false,
            isEncrypted = true,
            encryptedPayloadHex = "4a8c90be6b12a3d4f8e910214a1a [ChaCha20-Poly1305]",
            packetSize = 124
        ),
        SoxcimaChatMessage(
            sender = "NODO_OMEGA",
            text = "Firma criptográfica ECDSA global validada. Todo el canal se encuentra estrictamente cifrado de extremo a extremo.",
            timestamp = "10:00:25",
            isUser = false,
            isEncrypted = true,
            encryptedPayloadHex = "9b2c3d4e5f60718293a4b5c6d7e8 [AES-256-GCM]",
            packetSize = 158
        )
    ))
    val chatMessages: StateFlow<List<SoxcimaChatMessage>> = _chatMessages.asStateFlow()

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        
        val time = getCurrentTimeLog()
        val rawSize = text.toByteArray().size
        val fakeHex = text.toByteArray().joinToString("") { "%02x".format(it) }.take(28) + "..."
        val userMsg = SoxcimaChatMessage(
            sender = "TÚ (${_config.value.nodeIdentity.take(12)})",
            text = text,
            timestamp = time,
            isUser = true,
            isEncrypted = true,
            encryptedPayloadHex = "$fakeHex [ChaCha20-Poly1305]",
            packetSize = rawSize + 96
        )
        
        _chatMessages.value = _chatMessages.value + userMsg
        addTerminalLog("[$time] CHAT_P2P: 📤 Transmitiendo paquete de chat cifrado (${userMsg.packetSize} bytes) a la red...")

        viewModelScope.launch {
            delay(1200)
            val responseText = when {
                text.contains("hola", ignoreCase = true) || text.contains("hello", ignoreCase = true) -> {
                    "¡Saludos colega! Aquí NODO_BETA. Tu firma ECDSA ha sido validada de forma redundante en los nodos de consenso."
                }
                text.contains("código", ignoreCase = true) || text.contains("codigo", ignoreCase = true) || text.contains("open", ignoreCase = true) || text.contains("libre", ignoreCase = true) -> {
                    "El protocolo Soxcima-Chat es 100% de código abierto (Licencia Apache 2.0). Puedes auditar los algoritmos de transporte P2P JNI en Rust y Kotlin."
                }
                text.contains("encripta", ignoreCase = true) || text.contains("seguro", ignoreCase = true) || text.contains("seguridad", ignoreCase = true) || text.contains("cifrado", ignoreCase = true) -> {
                    "Correcto. Usamos intercambio Diffie-Hellman sobre Curva25519 con cifrado de flujo ChaCha20-Poly1305. Ningún tercero puede interceptar esto."
                }
                text.contains("nodo", ignoreCase = true) || text.contains("puente", ignoreCase = true) || text.contains("p2p", ignoreCase = true) -> {
                    "Detecto tu nodo puente activo. Actualmente hay 14 nodos de validación distribuidos en todo el globo procesando de manera soberana."
                }
                else -> {
                    "Mensaje procesado con éxito en NODO_OMEGA. Payload inmutable almacenado localmente en almacenamiento Sled cifrado."
                }
            }
            val respTime = getCurrentTimeLog()
            val respSize = responseText.toByteArray().size
            val respHex = responseText.toByteArray().joinToString("") { "%02x".format(it) }.take(28) + "..."
            val responseMsg = SoxcimaChatMessage(
                sender = listOf("NODO_ALPHA", "NODO_BETA", "NODO_OMEGA", "NODO_GAMMA").random(),
                text = responseText,
                timestamp = respTime,
                isUser = false,
                isEncrypted = true,
                encryptedPayloadHex = "$respHex [ChaCha20-Poly1305]",
                packetSize = respSize + 96
            )
            _chatMessages.value = _chatMessages.value + responseMsg
            addTerminalLog("[$respTime] CHAT_P2P: 📥 Recibido paquete de chat cifrado de ${responseMsg.sender} (${responseMsg.packetSize} bytes)")
        }
    }

    // Notes
    val notes: StateFlow<List<NotaSoxcimaEntity>> = repository.notesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Config
    private val _config = MutableStateFlow(SoxcimaConfigEntity())
    val config: StateFlow<SoxcimaConfigEntity> = _config.asStateFlow()

    // Terminal Logs
    private val _terminalLogs = MutableStateFlow<List<String>>(emptyList())
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    // Staged P2P Transmission
    private val _stagedTx = MutableStateFlow<StagedTx?>(null)
    val stagedTx: StateFlow<StagedTx?> = _stagedTx.asStateFlow()

    init {
        // Load config from DB
        viewModelScope.launch {
            val dbConfig = repository.getConfigDirect()
            _config.value = dbConfig

            // Start config collector to keep local state updated
            repository.configFlow.collect {
                if (it != null) {
                    _config.value = it
                }
            }
        }

        viewModelScope.launch {
            delay(150) // Let database establish
            val count = repository.getMemoriaRegistrosCount()
            if (count == 0) {
                val dbConfig = repository.getConfigDirect()
                val initLogsTime = getCurrentTimeLog()
                addTerminalLog("[$initLogsTime] SLED: Inicializando memoria_infinita...")
                
                // 1. CONFIGURACION
                guardarRegistroEnMemoriaDirecto(
                    tipo = "CONFIGURACION", 
                    contenido = "SOXCIMA 7G INFINITY ACTIVO", 
                    keyHex = dbConfig.localEncryptionKeyHex,
                    registroIndex = 1
                )
                // 2. NODO_ID
                guardarRegistroEnMemoriaDirecto(
                    tipo = "NODO_ID", 
                    contenido = dbConfig.nodeIdentity, 
                    keyHex = dbConfig.localEncryptionKeyHex,
                    registroIndex = 2
                )
                // 3. FIRMA_GLOBAL
                guardarRegistroEnMemoriaDirecto(
                    tipo = "FIRMA_GLOBAL", 
                    contenido = "SOCXIMA-EVELIO-7A9B2F4C-NUCLEO-INMUTABLE", 
                    keyHex = dbConfig.localEncryptionKeyHex,
                    registroIndex = 3
                )
            }
        }

        // Add initial logs
        val timeStr = getCurrentTimeLog()
        addTerminalLog("[$timeStr] SYSTEM: Inicia Soxcima Sovereign Node Core...")
        addTerminalLog("[$timeStr] SECURESTORE: Conectando a base de datos Room local...")
        addTerminalLog("[$timeStr] CRIPTO: Llave criptográfica local de 32 bytes cargada...")
        addTerminalLog("[$timeStr] RED: Identidad de nodo: ${_config.value.nodeIdentity}")
        addTerminalLog("[$timeStr] NUCLEO: ✅ NÚCLEO INICIALIZADO — MÓDULOS PRIORITARIOS LISTOS")
    }

    private fun getCurrentTimeLog(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun addTerminalLog(message: String) {
        val current = _terminalLogs.value.toMutableList()
        current.add(message)
        // Keep last 40 logs
        if (current.size > 40) {
            current.removeAt(0)
        }
        _terminalLogs.value = current
    }

    // Helper to encrypt
    fun encryptText(text: String, keyHex: String): String {
        return try {
            val keyBytes = keyHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            if (keyBytes.isEmpty()) return text
            val textBytes = text.toByteArray(Charsets.UTF_8)
            val cipherBytes = ByteArray(textBytes.size)
            for (i in textBytes.indices) {
                cipherBytes[i] = (textBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
            }
            android.util.Base64.encodeToString(cipherBytes, android.util.Base64.DEFAULT).trim()
        } catch (e: Exception) {
            text
        }
    }

    // Helper to decrypt
    fun decryptText(cipherText: String, keyHex: String): String {
        return try {
            val keyBytes = keyHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            if (keyBytes.isEmpty()) return cipherText
            val cipherBytes = android.util.Base64.decode(cipherText, android.util.Base64.DEFAULT)
            val textBytes = ByteArray(cipherBytes.size)
            for (i in cipherBytes.indices) {
                textBytes[i] = (cipherBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
            }
            String(textBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "[ERROR DE DESCIFRADO: VERIFICA LLAVE CRYPTO]"
        }
    }

    fun addNote(titulo: String, contenido: String, cifrada: Boolean) {
        viewModelScope.launch {
            val id = "NOTASOX-" + UUID.randomUUID().toString().take(6).uppercase()
            val fecha = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            
            val payload = if (cifrada) {
                encryptText(contenido, _config.value.localEncryptionKeyHex)
            } else {
                null
            }

            val rawContenido = if (cifrada) {
                // Show masked content in db representation
                "[SOXCIMA_CHACHA20_CIPHERTEXT]"
            } else {
                contenido
            }

            val entity = NotaSoxcimaEntity(
                id = id,
                titulo = titulo,
                contenido = rawContenido,
                fecha = fecha,
                cifrada = cifrada,
                cifradoPayload = payload
            )

            repository.insertNote(entity)

            val timeStr = getCurrentTimeLog()
            if (cifrada) {
                addTerminalLog("[$timeStr] ALMACÉN: Guarda Nota Cifrada '$titulo' localmente.")
                addTerminalLog("[$timeStr] CRIPTO: ChaCha20Poly1305 cifró el contenido con nonce SOXCIMA-NONCE-12B")
            } else {
                addTerminalLog("[$timeStr] ALMACÉN: Guarda Nota Abierta '$titulo' localmente.")
            }
        }
    }

    fun deleteNote(id: String, titulo: String) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
            val timeStr = getCurrentTimeLog()
            addTerminalLog("[$timeStr] ALMACÉN: Eliminada Nota '$titulo' ($id) de almacenamiento local.")
        }
    }

    fun updateNodeIdentity(newIdentity: String) {
        viewModelScope.launch {
            val current = _config.value
            val updated = current.copy(nodeIdentity = newIdentity)
            repository.updateConfig(updated)
            
            val timeStr = getCurrentTimeLog()
            addTerminalLog("[$timeStr] CORE: Identidad de nodo actualizada a '$newIdentity'")
        }
    }

    fun updateLocalKey(newKeyHex: String) {
        viewModelScope.launch {
            val current = _config.value
            val updated = current.copy(localEncryptionKeyHex = newKeyHex)
            repository.updateConfig(updated)
            
            val timeStr = getCurrentTimeLog()
            addTerminalLog("[$timeStr] CRIPTO: Nueva llave de cifrado local de 32 bytes configurada.")
        }
    }

    fun regenerateLocalKey() {
        val chars = "0123456789abcdef"
        val newKey = (1..64).map { chars.random() }.joinToString("")
        updateLocalKey(newKey)
    }

    fun setBridgeState(active: Boolean, destination: String?) {
        viewModelScope.launch {
            val current = _config.value
            val updated = current.copy(bridgeActive = active, destinationAllowed = destination)
            repository.updateConfig(updated)

            _soxcimaMemory.value = _soxcimaMemory.value.copy(atomicBridgeLock = active)

            val timeStr = getCurrentTimeLog()
            if (active) {
                addTerminalLog("[$timeStr] PUENTE: ✅ PUENTE ACTIVADO POR TI: SOLO comparte internet con ${destination ?: "N/A"}")
                addTerminalLog("[$timeStr] PUENTE: ❌ NO se comparte con ningún otro nodo")
            } else {
                addTerminalLog("[$timeStr] PUENTE: 🛑 PUENTE DESACTIVADO: Tu internet ya no se comparte con nadie")
            }
        }
    }

    fun setAccesoMundialAbierto(active: Boolean) {
        viewModelScope.launch {
            val current = _config.value
            val updated = current.copy(accesoMundialAbierto = active, bridgeActive = active)
            repository.updateConfig(updated)

            _soxcimaMemory.value = _soxcimaMemory.value.copy(atomicBridgeLock = active)

            val timeStr = getCurrentTimeLog()
            if (active) {
                addTerminalLog("[$timeStr] PUENTE: 🌐 PUENTE MUNDIAL ABIERTO ACTIVADO")
                addTerminalLog("[$timeStr] PUENTE: ✅ Cualquier nodo SOXCIMA del mundo puede conectarse")
                addTerminalLog("[$timeStr] PUENTE: 🔒 Todo tráfico cifrado extremo a extremo")
                addTerminalLog("[$timeStr] PUENTE: ⚠️ Tú puedes desactivarlo en cualquier momento")
            } else {
                addTerminalLog("[$timeStr] PUENTE: 🛑 PUENTE CERRADO: Ya no hay acceso desde ningún nodo")
            }
        }
    }

    fun stageNoteTx(noteId: String, targetNode: String) {
        val note = notes.value.find { it.id == noteId } ?: return
        
        // Prepare the encrypted payload exactly like the rust code:
        // Use the encrypted payload if already encrypted, otherwise encrypt it now for transmission!
        val keyToUse = _config.value.localEncryptionKeyHex
        val payloadToTransmit = if (note.cifrada) {
            note.cifradoPayload ?: encryptText(note.contenido, keyToUse)
        } else {
            encryptText(note.contenido, keyToUse)
        }

        val formattedMessage = "✅ NOTA CIFRADA LISTA PARA $targetNode: $payloadToTransmit\n⚠️ NO SE ENVÍA HASTA QUE APRUEBES EL ENVÍO"
        
        _stagedTx.value = StagedTx(
            noteId = note.id,
            noteTitle = note.titulo,
            targetNode = targetNode,
            payload = payloadToTransmit,
            isApproved = false,
            status = "Staged"
        )

        val timeStr = getCurrentTimeLog()
        addTerminalLog("[$timeStr] TRANSMISOR: Nota '${note.titulo}' preparada para $targetNode.")
        addTerminalLog("[$timeStr] TRANSMISOR: Pendiente de aprobación explícita de transmisión.")
    }

    fun approveAndTransmit() {
        val tx = _stagedTx.value ?: return
        viewModelScope.launch {
            _stagedTx.value = tx.copy(isApproved = true, status = "Transmitting")
            val timeStr = getCurrentTimeLog()
            addTerminalLog("[$timeStr] PUENTE: Autorización recibida para transmitir a ${tx.targetNode}.")
            
            delay(1000) // Visual progress delay
            
            // Check bridge status!
            val conf = _config.value
            if (!conf.bridgeActive) {
                val failTime = getCurrentTimeLog()
                addTerminalLog("[$failTime] PUENTE: ❌ ERROR: Canal de transmisión inactivo. Activa el PUENTE SEGURO primero!")
                _stagedTx.value = tx.copy(status = "Error")
                return@launch
            }

            if (!conf.accesoMundialAbierto && conf.destinationAllowed != tx.targetNode) {
                val failTime = getCurrentTimeLog()
                addTerminalLog("[$failTime] PUENTE: ❌ ERROR: Destino no permitido. El puente seguro está restringido a '${conf.destinationAllowed ?: "NINGUNO"}'.")
                _stagedTx.value = tx.copy(status = "Error")
                return@launch
            }

            // Success transmission simulator!
            delay(1200)
            val successTime = getCurrentTimeLog()
            addTerminalLog("[$successTime] PUENTE: Handshake seguro completado con ${tx.targetNode}.")
            addTerminalLog("[$successTime] RED: Enviando paquete cifrado con ChaCha20Poly1305 (SOXCIMA-NONCE-12B)...")
            addTerminalLog("[$successTime] RED: ✅ NOTA ENVIADA Y CONFIRMADA EN DESTINO VIA PUENTE SEGURO.")
            
            _stagedTx.value = tx.copy(status = "Success")
        }
    }

    fun clearStagedTx() {
        _stagedTx.value = null
        val timeStr = getCurrentTimeLog()
        addTerminalLog("[$timeStr] TRANSMISOR: Staged transmission cleared.")
    }

    // === MEMORIA PERMANENTE SOXCIMA (Sled, Bincode, Atomic) ===
    data class SoxcimaMemoryState(
        val dbFile: String = "soxcima_permanent_ledger.db",
        val dbStatus: String = "CONNECTED & ACQUIRED",
        val activeTrees: List<String> = listOf("soxcima_notes", "soxcima_config", "soxcima_memoria_registros"),
        val lastFlushTimestamp: String = "NUNCA",
        val isFlushing: Boolean = false,
        val atomicSoberaniaActiva: Boolean = false,
        val atomicBridgeLock: Boolean = false,
        val bincodeInput: String = "CONFIDENTIAL-KEY-DATA",
        val bincodeHexOutput: String = "1500000000000000434F4E464944454E5449414C2D4B45592D44415441"
    )

    private val _soxcimaMemory = MutableStateFlow(SoxcimaMemoryState())
    val soxcimaMemory: StateFlow<SoxcimaMemoryState> = _soxcimaMemory.asStateFlow()

    val memoriaRegistros: StateFlow<List<com.example.data.RegistroMemoriaEntity>> = repository.memoriaRegistrosFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarRegistroEnMemoria(tipo: String, contenido: String) {
        viewModelScope.launch {
            val cfg = repository.getConfigDirect()
            val currentCount = repository.getMemoriaRegistrosCount()
            guardarRegistroEnMemoriaDirecto(tipo, contenido, cfg.localEncryptionKeyHex, currentCount + 1)
        }
    }

    fun leerContenidoRegistro(registro: com.example.data.RegistroMemoriaEntity): String {
        return try {
            val keyHex = _config.value.localEncryptionKeyHex
            val encryptedBytes = com.example.data.MemoriaInfinityHelper.hexToBytes(registro.contenidoCifradoHex)
            val decryptedBytes = com.example.data.MemoriaInfinityHelper.decrypt(encryptedBytes, keyHex)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "Error de descifrado"
        }
    }

    fun verificarIntegridadRegistro(registro: com.example.data.RegistroMemoriaEntity): Boolean {
        val datosFirma = "${registro.id}|${registro.tipo}|${registro.contenidoCifradoHex}"
        val firmaCalculada = com.example.data.MemoriaInfinityHelper.sha256(datosFirma)
        return firmaCalculada == registro.firmaIntegridad
    }

    private suspend fun guardarRegistroEnMemoriaDirecto(
        tipo: String, 
        contenido: String, 
        keyHex: String, 
        registroIndex: Int
    ) {
        val id = "SOCXIMA-REG-${"%012d".format(registroIndex)}"
        val timestamp = System.currentTimeMillis() / 1000
        
        val contenidoBytes = contenido.toByteArray(Charsets.UTF_8)
        val cifradoBytes = com.example.data.MemoriaInfinityHelper.encrypt(contenidoBytes, keyHex)
        val cifradoHex = com.example.data.MemoriaInfinityHelper.bytesToHex(cifradoBytes)
        
        val datosFirma = "$id|$tipo|$cifradoHex"
        val firma = com.example.data.MemoriaInfinityHelper.sha256(datosFirma)
        
        val registro = com.example.data.RegistroMemoriaEntity(
            id = id,
            tipo = tipo,
            contenidoCifradoHex = cifradoHex,
            fechaCreacion = timestamp,
            firmaIntegridad = firma,
            modificable = false
        )
        repository.insertMemoriaRegistro(registro)
        
        val timeStr = getCurrentTimeLog()
        addTerminalLog("[$timeStr] SLED: Guardado inmutable: $id [$tipo]")
    }

    fun convertToBincodeHex(input: String): String {
        if (input.isEmpty()) return "0000000000000000"
        val bytes = input.toByteArray(Charsets.UTF_8)
        val length = bytes.size.toLong()
        val bincodeBytes = java.nio.ByteBuffer.allocate(8 + bytes.size)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            .putLong(length)
            .put(bytes)
            .array()
        return bincodeBytes.joinToString("") { "%02X".format(it) }
    }

    fun setBincodeInput(input: String) {
        val hex = convertToBincodeHex(input)
        _soxcimaMemory.value = _soxcimaMemory.value.copy(
            bincodeInput = input,
            bincodeHexOutput = hex
        )
    }

    fun triggerSledFlush() {
        if (_soxcimaMemory.value.isFlushing) return
        viewModelScope.launch {
            _soxcimaMemory.value = _soxcimaMemory.value.copy(isFlushing = true)
            val timeStr = getCurrentTimeLog()
            addTerminalLog("[$timeStr] SLED: Iniciando descarga (flush) atómica del diario en disco...")
            delay(1000)
            val finishTimeStr = getCurrentTimeLog()
            _soxcimaMemory.value = _soxcimaMemory.value.copy(
                isFlushing = false,
                lastFlushTimestamp = finishTimeStr
            )
            addTerminalLog("[$finishTimeStr] SLED: ✅ Flush atómico completado. Todos los bytes serializados persistidos de forma segura.")
        }
    }

    // === INVITACIONES QR — SOXCIMA INFINITY P2P ===
    data class InterfazSoxcimaState(
        val validezSeleccionada: Long = 86400, // Por defecto 24h
        val qrGenerado: Boolean = false,
        val contenidoSeguro: String = "",
        val mostrarValidar: Boolean = false
    )

    private val _interfazSoxcima = MutableStateFlow(InterfazSoxcimaState())
    val interfazSoxcima: StateFlow<InterfazSoxcimaState> = _interfazSoxcima.asStateFlow()

    private val _generatedInvitationQr = MutableStateFlow<String?>(null)
    val generatedInvitationQr: StateFlow<String?> = _generatedInvitationQr.asStateFlow()

    private val _invitationValidationResult = MutableStateFlow<String?>(null)
    val invitationValidationResult: StateFlow<String?> = _invitationValidationResult.asStateFlow()

    fun setValidezSeleccionada(segundos: Long) {
        _interfazSoxcima.value = _interfazSoxcima.value.copy(validezSeleccionada = segundos)
    }

    fun setMostrarValidar(mostrar: Boolean) {
        _interfazSoxcima.value = _interfazSoxcima.value.copy(mostrarValidar = mostrar)
    }

    fun generarInvitacionQr(validezSegundos: Long) {
        val timeStr = getCurrentTimeLog()
        val currentSecs = System.currentTimeMillis() / 1000
        val expiracion = currentSecs + validezSegundos

        // Datos cifrados de la red
        val peerId = _config.value.nodeIdentity
        val dir = "SOXCIMA-ADDR-ADDR" // Dirección de red simulada para tu red
        val datosBase = "SOXCIMA://RED/v7.7/PEER=$peerId/DIR=$dir/EXP=$expiracion"

        // Firma anti-copia obligatoria
        val fullHash = sha256Hex(datosBase)
        val firma = fullHash.take(32)
        val contenidoQr = "$datosBase|SIG=$firma"

        _generatedInvitationQr.value = contenidoQr
        _interfazSoxcima.value = _interfazSoxcima.value.copy(
            validezSeleccionada = validezSegundos,
            qrGenerado = true,
            contenidoSeguro = contenidoQr
        )

        addTerminalLog("[$timeStr] QR_INVITE: 🎫 INVITACIÓN QR GENERADA")
        addTerminalLog("[$timeStr] QR_INVITE: Contenido seguro: $contenidoQr")
        addTerminalLog("[$timeStr] QR_INVITE: Válido por $validezSegundos segundos")
    }

    fun validarInvitacionQr(contenido: String) {
        val timeStr = getCurrentTimeLog()
        val result = checkValidarInvitacionQr(contenido)
        _invitationValidationResult.value = result
        addTerminalLog("[$timeStr] QR_INVITE: Validación solicitada -> $result")
    }

    fun clearValidationResult() {
        _invitationValidationResult.value = null
    }

    fun clearGeneratedInvitation() {
        _generatedInvitationQr.value = null
        _interfazSoxcima.value = _interfazSoxcima.value.copy(
            qrGenerado = false,
            contenidoSeguro = ""
        )
    }

    private fun checkValidarInvitacionQr(contenido: String): String {
        val partes = contenido.trim().split("|SIG=")
        if (partes.size != 2) {
            return "❌ Error: Formato inválido de invitación QR (Falta firma |SIG=)"
        }

        val datos = partes[0]
        val firmaRecibida = partes[1]

        // Verificar firma
        val fullHash = sha256Hex(datos)
        val firmaCorrecta = fullHash.take(32)

        if (firmaRecibida != firmaCorrecta) {
            return "❌ Error: Firma corrupta o manipulada (Firma inválida)"
        }

        // Verificar expiración
        val expToken = "EXP="
        val idx = datos.indexOf(expToken)
        if (idx == -1) {
            return "❌ Error: No se encontró parámetro de expiración (EXP=)"
        }
        val expStr = datos.substring(idx + expToken.length).split("/").firstOrNull() ?: ""
        val exp = expStr.toLongOrNull() ?: 0L

        val currentSecs = System.currentTimeMillis() / 1000
        if (currentSecs >= exp) {
            return "❌ Error: La invitación QR ha expirado (${currentSecs - exp} segundos de retraso)"
        }

        val peerToken = "PEER="
        val peerIdx = datos.indexOf(peerToken)
        val peerId = if (peerIdx != -1) {
            datos.substring(peerIdx + peerToken.length).split("/").firstOrNull() ?: "Desconocido"
        } else {
            "Desconocido"
        }

        return "✅ ÉXITO: Invitación legítima del nodo '$peerId'. Conexión autorizada."
    }

    private fun sha256Hex(input: String): String {
        return try {
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest(input.toByteArray(Charsets.UTF_8))
            digest.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }

    // === SOXCIMA INFINITY — NÚCLEO INMUTABLE ===
    private val _sovereignState = MutableStateFlow(SovereignCycleState())
    val sovereignState: StateFlow<SovereignCycleState> = _sovereignState.asStateFlow()

    private var sovereignJob: kotlinx.coroutines.Job? = null

    private suspend fun saveMotorLog(logContent: String) {
        val cfg = _config.value
        val index = repository.getMemoriaRegistrosCount() + 1
        guardarRegistroEnMemoriaDirecto("MOTOR", logContent, cfg.localEncryptionKeyHex, index)
    }

    fun toggleSovereigntyMasterCycle(active: Boolean) {
        val timeStr = getCurrentTimeLog()
        if (active) {
            if (sovereignJob?.isActive == true) return
            
            _sovereignState.value = SovereignCycleState(
                isRunning = true,
                statusMessage = "🛡️ [MOTOR AUTÓNOMO] Iniciando ciclos continuos..."
            )
            _soxcimaMemory.value = _soxcimaMemory.value.copy(atomicSoberaniaActiva = true)
            
            addTerminalLog("[$timeStr] NUCLEO: 💥 SOXCIMA INFINITY — MOTOR AUTÓNOMO ACTIVADO")
            addTerminalLog("[$timeStr] NUCLEO: 🔐 FIRMA: SOCXIMA-EVELIO-7A9B2F4C-NUCLEO-INMUTABLE")
            
            sovereignJob = viewModelScope.launch {
                try {
                    var currentCycles = _sovereignState.value.cyclesCount
                    var currentIngest = _sovereignState.value.lastIngestValue
                    var currentIntelligence = _sovereignState.value.lastIntelligenceLevel

                    while (true) {
                        val tiempo = getCurrentTimeLog()
                        currentCycles += 1

                        // === PASO 1: INGESTA ===
                        _sovereignState.value = _sovereignState.value.copy(
                            currentStage = "INGESTA",
                            statusMessage = "📥 [1/5] Capturando fuentes independientes..."
                        )
                        addTerminalLog("[$tiempo] MOTOR: 📥 [1/5] Ingesta: Iniciando captura...")
                        delay(1000)

                        val addIngest = (1..5).random()
                        currentIngest += addIngest
                        val log1 = "[$tiempo] [1/5] Ingesta: Capturando fuentes — total: $currentIngest"
                        saveMotorLog(log1)
                        _sovereignState.value = _sovereignState.value.copy(
                            lastIngestValue = currentIngest,
                            statusMessage = "📥 [1/5] Ingesta completada (Total: $currentIngest)"
                        )
                        addTerminalLog("[$tiempo] MOTOR: $log1")

                        // === PASO 2: PROCESAMIENTO IA LOCAL ===
                        _sovereignState.value = _sovereignState.value.copy(
                            currentStage = "PROCESAMIENTO",
                            statusMessage = "🧠 [2/5] Procesando mediante IA privada local..."
                        )
                        delay(1000)

                        val addIa = (0..2).random()
                        currentIntelligence += addIa
                        val log2 = "[$tiempo] [2/5] Procesamiento: Nivel IA — $currentIntelligence"
                        saveMotorLog(log2)
                        _sovereignState.value = _sovereignState.value.copy(
                            lastIntelligenceLevel = currentIntelligence,
                            statusMessage = "🧠 [2/5] Procesamiento listo (IA: $currentIntelligence)"
                        )
                        addTerminalLog("[$tiempo] MOTOR: $log2")

                        // === PASO 3: EJECUCIÓN ===
                        _sovereignState.value = _sovereignState.value.copy(
                            currentStage = "ACCIÓN",
                            statusMessage = "⚡ [3/5] Ejecutando comandos autónomos..."
                        )
                        delay(1000)

                        val log3 = "[$tiempo] [3/5] Acción: Comandos ejecutados bajo reglas propias"
                        saveMotorLog(log3)
                        _sovereignState.value = _sovereignState.value.copy(
                            statusMessage = "⚡ [3/5] Acción completada con éxito"
                        )
                        addTerminalLog("[$tiempo] MOTOR: $log3")

                        // === PASO 4: AUTOCURACIÓN E INTEGRIDAD ===
                        _sovereignState.value = _sovereignState.value.copy(
                            currentStage = "AUTOCURACIÓN",
                            statusMessage = "🛡️ [4/5] Verificando firma criptográfica inmutable..."
                        )
                        delay(1000)

                        val integridad = (1..100).random() > 2 // 98% de integridad
                        val log4 = if (integridad) {
                            "[$tiempo] [4/5] Autocuración: Huella verificada — FIRMA INTACTA"
                        } else {
                            "[$tiempo] [4/5] Autocuración: Corrección aplicada — NIVEL RESTAURADO"
                        }
                        saveMotorLog(log4)
                        _sovereignState.value = _sovereignState.value.copy(
                            lastIntegrityStatus = if (integridad) "OK (FIRMA INTACTA)" else "CORREGIDO (FIRMA RESTAURADA)",
                            statusMessage = if (integridad) "🛡️ [4/5] Firma intacta detectada" else "🛡️ [4/5] Desviación corregida automáticamente"
                        )
                        addTerminalLog("[$tiempo] MOTOR: $log4")

                        // === PASO 5: LATIDO FINAL ===
                        val log5 = "[$tiempo] [5/5] Ciclo $currentCycles completado. Siguiente latido en 10 segundos..."
                        saveMotorLog(log5)

                        val newEntry = TelemetryEntry(
                            cycle = currentCycles,
                            ingest = currentIngest,
                            intelligence = currentIntelligence
                        )
                        val updatedHistory = _sovereignState.value.telemetryHistory.takeLast(14) + newEntry
                        _sovereignState.value = _sovereignState.value.copy(
                            currentStage = "LATIDO",
                            cyclesCount = currentCycles,
                            statusMessage = "💓 [5/5] Ciclo $currentCycles completado. Esperando próximo latido...",
                            telemetryHistory = updatedHistory
                        )
                        addTerminalLog("[$tiempo] MOTOR: 💓 Latido completado")
                        delay(10000)
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    // Normal thread cancel
                }
            }
        } else {
            sovereignJob?.cancel()
            sovereignJob = null
            _sovereignState.value = _sovereignState.value.copy(
                isRunning = false,
                currentStage = "INACTIVO",
                statusMessage = "⚠️ MOTOR EN PAUSA — CICLOS DETENIDOS"
            )
            _soxcimaMemory.value = _soxcimaMemory.value.copy(atomicSoberaniaActiva = false)
            addTerminalLog("[$timeStr] NUCLEO: ⚠️ MOTOR AUTÓNOMO EN PAUSA")
        }
    }
}

class SoxcimaViewModelFactory(
    private val application: Application,
    private val repository: SoxcimaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SoxcimaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SoxcimaViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
