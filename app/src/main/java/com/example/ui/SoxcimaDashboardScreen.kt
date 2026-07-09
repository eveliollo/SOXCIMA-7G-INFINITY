package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.NotaSoxcimaEntity

@Composable
fun SoxcimaDashboardScreen(
    viewModel: SoxcimaViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val config by viewModel.config.collectAsStateWithLifecycle()
    val logs by viewModel.terminalLogs.collectAsStateWithLifecycle()
    val stagedTx by viewModel.stagedTx.collectAsStateWithLifecycle()
    val generatedInvitationQr by viewModel.generatedInvitationQr.collectAsStateWithLifecycle()
    val invitationValidationResult by viewModel.invitationValidationResult.collectAsStateWithLifecycle()
    val sovereignState by viewModel.sovereignState.collectAsStateWithLifecycle()
    val soxcimaMemory by viewModel.soxcimaMemory.collectAsStateWithLifecycle()
    val memoriaRegistros by viewModel.memoriaRegistros.collectAsStateWithLifecycle()
    val isLightMode by viewModel.isLightMode.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val bgMain = if (isLightMode) Color(0xFFF0F3F6) else Color(0xFF0F1115)
    val borderCol = if (isLightMode) Color(0xFFD1D5DB) else Color(0xFF1E2638)
    val txtSub = if (isLightMode) Color(0xFF4B5563) else Color.Gray

    var activeTab by remember { mutableIntStateOf(0) }

    // Dialog flags
    var showCreateNoteDialog by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showTransmitDialog by remember { mutableStateOf(false) }
    var noteToTransmit by remember { mutableStateOf<NotaSoxcimaEntity?>(null) }
    var selectedNoteForDetail by remember { mutableStateOf<NotaSoxcimaEntity?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(bgMain),
        containerColor = bgMain
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Hero Banner with Server Illustration & Title Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                // Dynamic Hero Background image (Generated)
                Image(
                    painter = painterResource(id = R.drawable.img_soxcima_banner_1783612829453),
                    contentDescription = "Soxcima Server Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.45f
                )

                // Shimmer Overlay Brush
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF0F1115).copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Sovereign Brand Text Overlays
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "SOXCIMA CORE",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    color = Color(0xFF00FF87), // Sovereign Neon Green
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Text(
                                text = "7G INFINITY sovereign secure node",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    color = Color.LightGray.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleLightMode() },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .size(38.dp)
                                    .testTag("theme_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isLightMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "Toggle Theme",
                                    tint = Color(0xFF00FF87),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { showConfigDialog = true },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .size(38.dp)
                                    .testTag("node_config_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Node Config",
                                    tint = Color(0xFF00FF87),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Identity Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161A23))
                    .border(1.dp, Color(0xFF1E2638).copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (config.bridgeActive) Color(0xFF00FF87) else Color(0xFFFFB300))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NODE: ${config.nodeIdentity}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 220.dp)
                    )
                }

                Text(
                    text = if (config.bridgeActive) "P2P PUENTE: ONLINE" else "P2P PUENTE: OFFLINE",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = if (config.bridgeActive) Color(0xFF00FF87) else Color.Gray,
                        fontSize = 11.sp
                    )
                )
            }

            // Tab bar
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = bgMain,
                contentColor = Color(0xFF00FF87),
                edgePadding = 8.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = Color(0xFF00FF87)
                    )
                },
                divider = { HorizontalDivider(color = borderCol) }
            ) {
                // Tab 0: ALMACÉN
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    modifier = Modifier.testTag("tab_notes")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (activeTab == 0) Color(0xFF00FF87) else txtSub,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ALMACÉN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 0) (if (isLightMode) Color(0xFF008A47) else Color.White) else txtSub
                            )
                        )
                    }
                }

                // Tab 1: CHAT P2P
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    modifier = Modifier.testTag("tab_chat")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = if (activeTab == 1) Color(0xFF00FF87) else txtSub,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "CHAT P2P",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) (if (isLightMode) Color(0xFF008A47) else Color.White) else txtSub
                            )
                        )
                    }
                }

                // Tab 2: PUENTE P2P
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    modifier = Modifier.testTag("tab_bridge")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Power,
                            contentDescription = null,
                            tint = if (activeTab == 2) Color(0xFF00FF87) else txtSub,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PUENTE P2P",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 2) (if (isLightMode) Color(0xFF008A47) else Color.White) else txtSub
                            )
                        )
                    }
                }

                // Tab 3: PORTAL WEB
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    modifier = Modifier.testTag("tab_portal")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (activeTab == 3) Color(0xFF00FF87) else txtSub,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PORTAL WEB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 3) (if (isLightMode) Color(0xFF008A47) else Color.White) else txtSub
                            )
                        )
                    }
                }

                // Tab 4: TERMINAL
                Tab(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    modifier = Modifier.testTag("tab_terminal")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = if (activeTab == 4) Color(0xFF00FF87) else txtSub,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TERMINAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 4) (if (isLightMode) Color(0xFF008A47) else Color.White) else txtSub
                            )
                        )
                    }
                }
            }

            // Tab contents
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (activeTab) {
                    0 -> NotesTabContent(
                        notes = notes,
                        soxcimaMemory = soxcimaMemory,
                        memoriaRegistros = memoriaRegistros,
                        isLightMode = isLightMode,
                        onBincodeInputChange = { viewModel.setBincodeInput(it) },
                        onFlushSled = { viewModel.triggerSledFlush() },
                        onNoteClick = { selectedNoteForDetail = it },
                        onDeleteClick = { viewModel.deleteNote(it.id, it.titulo) },
                        onTransmitClick = {
                            noteToTransmit = it
                            showTransmitDialog = true
                        },
                        onCreateNoteClick = { showCreateNoteDialog = true },
                        onGuardarRegistro = { tipo, contenido -> viewModel.guardarRegistroEnMemoria(tipo, contenido) },
                        onLeerContenido = { viewModel.leerContenidoRegistro(it) },
                        onVerificarIntegridad = { viewModel.verificarIntegridadRegistro(it) }
                    )
                    1 -> ChatTabContent(
                        chatMessages = chatMessages,
                        isLightMode = isLightMode,
                        onSendMessage = { viewModel.sendChatMessage(it) }
                    )
                    2 -> BridgeTabContent(
                        config = config,
                        stagedTx = stagedTx,
                        generatedInvitationQr = generatedInvitationQr,
                        invitationValidationResult = invitationValidationResult,
                        sovereignState = sovereignState,
                        isLightMode = isLightMode,
                        onToggleBridge = { active, dest -> viewModel.setBridgeState(active, dest) },
                        onToggleAccesoMundial = { viewModel.setAccesoMundialAbierto(it) },
                        onApproveTx = { viewModel.approveAndTransmit() },
                        onClearTx = { viewModel.clearStagedTx() },
                        onGenerarInvitacion = { viewModel.generarInvitacionQr(it) },
                        onValidarInvitacion = { viewModel.validarInvitacionQr(it) },
                        onClearValidationResult = { viewModel.clearValidationResult() },
                        onClearGeneratedInvitation = { viewModel.clearGeneratedInvitation() },
                        onToggleSovereign = { viewModel.toggleSovereigntyMasterCycle(it) }
                    )
                    3 -> WebTabContent(
                        isLightMode = isLightMode,
                        config = config,
                        sovereignState = sovereignState
                    )
                    4 -> TerminalTabContent(
                        logs = logs,
                        nodeIdentity = config.nodeIdentity,
                        isLightMode = isLightMode,
                        onClearLogs = { viewModel.addTerminalLog("[SYSTEM] Terminal cleared.") }
                    )
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Create Note Dialog
    if (showCreateNoteDialog) {
        var titulo by remember { mutableStateOf("") }
        var contenido by remember { mutableStateOf("") }
        var isCifrada by remember { mutableStateOf(true) } // Encrypted by default to match sovereign network

        AlertDialog(
            onDismissRequest = { showCreateNoteDialog = false },
            containerColor = Color(0xFF161A23),
            title = {
                Text(
                    text = "NUEVA NOTA SOVEREIGN",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FF87),
                        fontFamily = FontFamily.Monospace
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título de la Nota") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF87),
                            unfocusedBorderColor = Color(0xFF2E384E),
                            focusedLabelColor = Color(0xFF00FF87),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("note_title_input")
                    )

                    OutlinedTextField(
                        value = contenido,
                        onValueChange = { contenido = it },
                        label = { Text("Contenido / Secreto") },
                        minLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF87),
                            unfocusedBorderColor = Color(0xFF2E384E),
                            focusedLabelColor = Color(0xFF00FF87),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("note_content_input")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1115))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCifrada) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isCifrada) Color(0xFF00FF87) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Cifrado Local",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Cifra con ChaCha20Poly1305",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                )
                            }
                        }

                        Switch(
                            checked = isCifrada,
                            onCheckedChange = { isCifrada = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00FF87),
                                checkedTrackColor = Color(0xFF00FF87).copy(alpha = 0.3f),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier.testTag("note_encrypt_switch")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titulo.isNotBlank() && contenido.isNotBlank()) {
                            viewModel.addNote(titulo, contenido, isCifrada)
                            showCreateNoteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87), contentColor = Color.Black),
                    modifier = Modifier.testTag("submit_note_button")
                ) {
                    Text("GUARDAR LOCAL", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCreateNoteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.LightGray)
                ) {
                    Text("CANCELAR")
                }
            }
        )
    }

    // 2. Node Config & Key Dialog
    if (showConfigDialog) {
        var identityInput by remember { mutableStateOf(config.nodeIdentity) }
        var keyInput by remember { mutableStateOf(config.localEncryptionKeyHex) }
        var showRawKey by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            containerColor = Color(0xFF161A23),
            title = {
                Text(
                    text = "SOVEREIGN CONFIG",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FF87),
                        fontFamily = FontFamily.Monospace
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = identityInput,
                        onValueChange = { identityInput = it },
                        label = { Text("Node Identity (Firma)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF87),
                            unfocusedBorderColor = Color(0xFF2E384E),
                            focusedLabelColor = Color(0xFF00FF87),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("config_identity_input")
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1115))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = Color(0xFF00FF87),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "32-Byte Secret Key",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = { showRawKey = !showRawKey },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showRawKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle key visible",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.regenerateLocalKey() },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Regenerate key",
                                        tint = Color(0xFF00FF87),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (showRawKey) config.localEncryptionKeyHex else "••••••••••••••••••••••••••••••••••••••••••••••••",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = if (showRawKey) Color(0xFF00FF87) else Color.Gray,
                                fontSize = 11.sp
                            ),
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SoxcimaQrCode(
                            text = config.nodeIdentity,
                            modifier = Modifier.size(110.dp)
                        )
                    }
                    Text(
                        text = "Escanea este código QR para autenticar firma P2P",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (identityInput.isNotBlank()) {
                            viewModel.updateNodeIdentity(identityInput)
                            showConfigDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87), contentColor = Color.Black),
                    modifier = Modifier.testTag("submit_config_button")
                ) {
                    Text("APLICAR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfigDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.LightGray)
                ) {
                    Text("CERRAR")
                }
            }
        )
    }

    // 3. Stage & Transmit Dialog
    if (showTransmitDialog && noteToTransmit != null) {
        var destinationInput by remember { mutableStateOf(config.destinationAllowed ?: "") }

        AlertDialog(
            onDismissRequest = { showTransmitDialog = false },
            containerColor = Color(0xFF161A23),
            title = {
                Text(
                    text = "PREPARAR TRANSMISIÓN P2P",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FF87),
                        fontFamily = FontFamily.Monospace
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Nota a Enviar: ${noteToTransmit?.titulo}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )

                    OutlinedTextField(
                        value = destinationInput,
                        onValueChange = { destinationInput = it },
                        label = { Text("Nodo Destino ID") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF87),
                            unfocusedBorderColor = Color(0xFF2E384E),
                            focusedLabelColor = Color(0xFF00FF87),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("dest_node_input")
                    )

                    Text(
                        text = "⚠️ La transmisión se enviará de forma cifrada. Requiere que el Puente P2P esté ACTIVO y configurado para este destino en la pestaña de Puente.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFFB300)),
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (destinationInput.isNotBlank()) {
                            viewModel.stageNoteTx(noteToTransmit!!.id, destinationInput)
                            showTransmitDialog = false
                            activeTab = 1 // Switch to bridge tab to view staged Tx!
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87), contentColor = Color.Black),
                    modifier = Modifier.testTag("confirm_stage_button")
                ) {
                    Text("PREPARAR ENVÍO", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTransmitDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.LightGray)
                ) {
                    Text("CANCELAR")
                }
            }
        )
    }

    // 4. Note Details & Decryption Dialog
    if (selectedNoteForDetail != null) {
        val note = selectedNoteForDetail!!
        var enterPasskey by remember { mutableStateOf("") }
        var decryptedContent by remember { mutableStateOf<String?>(null) }
        var hasTriedDecrypt by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { selectedNoteForDetail = null },
            containerColor = Color(0xFF161A23),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = note.titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (note.cifrada) Color(0xFF00FF87).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (note.cifrada) "CIFRADA" else "ABIERTA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (note.cifrada) Color(0xFF00FF87) else Color.LightGray,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ID: ${note.id}  •  Creada: ${note.fecha}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontFamily = FontFamily.Monospace)
                    )

                    HorizontalDivider(color = Color(0xFF2E384E))

                    if (note.cifrada) {
                        if (decryptedContent == null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F1115))
                                    .border(1.dp, Color(0xFF2E384E))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Contenido Protegido",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = note.contenido, // Holds the cipher mask "[SOXCIMA_CHACHA20_CIPHERTEXT]"
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Gray
                                    )
                                )

                                if (note.cifradoPayload != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Payload Cifrado Base64:",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                    )
                                    Text(
                                        text = note.cifradoPayload.take(45) + "...",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF00FF87)
                                        )
                                    )
                                }
                            }

                            // Passphrase input
                            OutlinedTextField(
                                value = enterPasskey,
                                onValueChange = { enterPasskey = it },
                                label = { Text("Llave de Descifrado Hex (32 bytes)") },
                                placeholder = { Text("Por defecto usa llave local") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00FF87),
                                    unfocusedBorderColor = Color(0xFF2E384E),
                                    focusedLabelColor = Color(0xFF00FF87),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.LightGray
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("decrypt_key_input")
                            )

                            Button(
                                onClick = {
                                    val keyToUse = if (enterPasskey.isBlank()) config.localEncryptionKeyHex else enterPasskey
                                    if (note.cifradoPayload != null) {
                                        val res = viewModel.decryptText(note.cifradoPayload, keyToUse)
                                        decryptedContent = res
                                    }
                                    hasTriedDecrypt = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87), contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth().testTag("decrypt_submit_button")
                            ) {
                                Text("DESCIFRAR CON CHACHA20", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Decrypted content view
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F1115).copy(alpha = 0.5f))
                                    .border(1.dp, Color(0xFF00FF87))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = Color(0xFF00FF87),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Contenido Descifrado:",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00FF87)
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = decryptedContent ?: "",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                                )
                            }
                        }
                    } else {
                        // Open note content directly
                        Text(
                            text = note.contenido,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedNoteForDetail = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF00FF87))
                ) {
                    Text("CERRAR")
                }
            }
        )
    }
}

// --- TAB CONTENT COMPOSABLES ---

@Composable
fun NotesTabContent(
    notes: List<NotaSoxcimaEntity>,
    soxcimaMemory: com.example.ui.SoxcimaViewModel.SoxcimaMemoryState,
    memoriaRegistros: List<com.example.data.RegistroMemoriaEntity>,
    isLightMode: Boolean,
    onBincodeInputChange: (String) -> Unit,
    onFlushSled: () -> Unit,
    onNoteClick: (NotaSoxcimaEntity) -> Unit,
    onDeleteClick: (NotaSoxcimaEntity) -> Unit,
    onTransmitClick: (NotaSoxcimaEntity) -> Unit,
    onCreateNoteClick: () -> Unit,
    onGuardarRegistro: (String, String) -> Unit,
    onLeerContenido: (com.example.data.RegistroMemoriaEntity) -> String,
    onVerificarIntegridad: (com.example.data.RegistroMemoriaEntity) -> Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NOTAS EN ALMACÉN LOCAL",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )

            Button(
                onClick = onCreateNoteClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87), contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp).testTag("add_note_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CREAR NOTA", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No hay notas guardadas localmente.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Text(
                    text = "Toca 'CREAR NOTA' para guardar un secreto soberano.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    Card(
                        onClick = { onNoteClick(note) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A23)),
                        border = borderStrokeForNote(note.cifrada),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_card_${note.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (note.cifrada) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = if (note.cifrada) "Encrypted note" else "Plain text note",
                                        tint = if (note.cifrada) Color(0xFF00FF87) else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = note.titulo,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 160.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onTransmitClick(note) },
                                        modifier = Modifier.size(32.dp).testTag("transmit_note_button_${note.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Stage Send note",
                                            tint = Color(0xFF00FF87),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onDeleteClick(note) },
                                        modifier = Modifier.size(32.dp).testTag("delete_note_button_${note.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete note",
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (note.cifrada) "[ CONTENIDO CIFRADO CHACHA20POLY1305 ]" else note.contenido,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (note.cifrada) Color(0xFF00FF87).copy(alpha = 0.6f) else Color.LightGray.copy(alpha = 0.8f),
                                    fontFamily = if (note.cifrada) FontFamily.Monospace else FontFamily.Default
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Firma ID: ${note.id}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray
                                    )
                                )
                                Text(
                                    text = note.fecha,
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SECCIÓN: MEMORIA PERMANENTE SOXCIMA (SLED & BINCODE) ---
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161A23)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E384E)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("soxcima_permanent_memory_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFF00FF87),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "MEMORIA PERMANENTE SOXCIMA",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Base de Datos Inmutable (Sled, Bincode, Atomics)",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                        }
                    }

                    Button(
                        onClick = onFlushSled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (soxcimaMemory.isFlushing) Color.Gray else Color(0xFF1E2638),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("sled_flush_button"),
                        enabled = !soxcimaMemory.isFlushing
                    ) {
                        Text(
                            text = if (soxcimaMemory.isFlushing) "GUARDANDO..." else "SLED FLUSH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF2E384E))

                // Metadata Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // DB FILE
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F1115))
                            .border(1.dp, Color(0xFF1E2638))
                            .padding(6.dp)
                    ) {
                        Text("ARCHIVO SLED", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 8.sp))
                        Text(
                            text = soxcimaMemory.dbFile,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // CONNECTED STATUS
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F1115))
                            .border(1.dp, Color(0xFF1E2638))
                            .padding(6.dp)
                    ) {
                        Text("ESTADO DRIVER", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 8.sp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(Color(0xFF00FF87))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = soxcimaMemory.dbStatus,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF87),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // Atomic states indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ATOMIC SOBERANIA
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F1115))
                            .border(1.dp, Color(0xFF1E2638))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ATOMIC SOBERANIA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (soxcimaMemory.atomicSoberaniaActiva) Color(0xFF00FF87).copy(alpha = 0.15f) else Color.DarkGray)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (soxcimaMemory.atomicSoberaniaActiva) "TRUE" else "FALSE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (soxcimaMemory.atomicSoberaniaActiva) Color(0xFF00FF87) else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }

                    // ATOMIC BRIDGE LOCK
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F1115))
                            .border(1.dp, Color(0xFF1E2638))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ATOMIC BRIDGE LOCK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (soxcimaMemory.atomicBridgeLock) Color(0xFFFF5252).copy(alpha = 0.15f) else Color.DarkGray)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (soxcimaMemory.atomicBridgeLock) "LOCKED" else "UNLOCKED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (soxcimaMemory.atomicBridgeLock) Color(0xFFFF5252) else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Último Flush Sled: ${soxcimaMemory.lastFlushTimestamp}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 9.sp)
                    )
                    Text(
                        text = "Motores: sled v0.34 | atomic v0.5",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.DarkGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    )
                }

                HorizontalDivider(color = Color(0xFF2E384E).copy(alpha = 0.5f))

                // === SECCIÓN: INSERTAR NUEVO REGISTRO EN EL LEDGER ===
                Text(
                    text = "GUARDAR REGISTRO INMUTABLE EN LEDGER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                )

                var customType by remember { androidx.compose.runtime.mutableStateOf("NOTA") }
                var customContent by remember { androidx.compose.runtime.mutableStateOf("") }

                // Type selector row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val availableTypes = listOf("NOTA", "CLAVE", "RED", "CONFIG", "QR", "VALIDACION")
                    availableTypes.forEach { type ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (customType == type) Color(0xFF00FF87).copy(alpha = 0.2f) else Color(0xFF0F1115))
                                .border(1.dp, if (customType == type) Color(0xFF00FF87) else Color(0xFF1E2638))
                                .clickable { customType = type }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (customType == type) Color(0xFF00FF87) else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customContent,
                        onValueChange = { customContent = it },
                        placeholder = { Text("Contenido secreto a cifrar...", color = Color.DarkGray, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF87),
                            unfocusedBorderColor = Color(0xFF2E384E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("ledger_custom_input_field")
                    )

                    Button(
                        onClick = {
                            if (customContent.isNotEmpty()) {
                                onGuardarRegistro(customType, customContent)
                                customContent = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF87),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("ledger_save_button"),
                        enabled = customContent.isNotEmpty()
                    ) {
                        Text(
                            text = "GUARDAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF2E384E).copy(alpha = 0.5f))

                // === SECCIÓN: HISTORIAL COMPLETO DEL LEDGER ===
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HISTORIAL DEL LEDGER DE MEMORIA INMUTABLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF00FF87).copy(alpha = 0.1f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${memoriaRegistros.size} REGISTROS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00FF87),
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                val decryptedRecords = remember { androidx.compose.runtime.mutableStateMapOf<String, String>() }

                if (memoriaRegistros.isEmpty()) {
                    Text(
                        text = "Cargando o memoria vacía...",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontFamily = FontFamily.Monospace)
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        memoriaRegistros.forEach { record ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1115)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2638)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = record.id,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Color(0xFF1E2638))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = record.tipo,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.LightGray,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 8.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            )
                                        }
                                    }

                                    // Hex payload
                                    Text(
                                        text = "HEX: ${record.contenidoCifradoHex.take(20)}...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.Gray,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp
                                        )
                                    )

                                    // Signature
                                    Text(
                                        text = "FIRMA: ${record.firmaIntegridad.take(16)}...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.DarkGray,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp
                                        )
                                    )

                                    if (decryptedRecords.containsKey(record.id)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF161A23))
                                                .border(1.dp, Color(0xFF00FF87).copy(alpha = 0.3f))
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = decryptedRecords[record.id] ?: "",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF00FF87),
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Integrity check status icon/text
                                        val isVerified = onVerificarIntegridad(record)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(if (isVerified) Color(0xFF00FF87).copy(alpha = 0.1f) else Color(0xFFFF5252).copy(alpha = 0.1f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (isVerified) "INTEGRIDAD: OK" else "INTEGRIDAD: FALLO",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isVerified) Color(0xFF00FF87) else Color(0xFFFF5252),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 8.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        TextButton(
                                            onClick = {
                                                if (decryptedRecords.containsKey(record.id)) {
                                                    decryptedRecords.remove(record.id)
                                                } else {
                                                    decryptedRecords[record.id] = onLeerContenido(record)
                                                }
                                            },
                                            contentPadding = PaddingValues(0.dp),
                                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF00FF87)),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text(
                                                text = if (decryptedRecords.containsKey(record.id)) "CIFRAR" else "DESCIFRAR",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF2E384E).copy(alpha = 0.5f))

                // Bincode Serialization Area
                Text(
                    text = "PATRÓN DE SERIALIZACIÓN BINCODE (bincode v1.3):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                )

                OutlinedTextField(
                    value = soxcimaMemory.bincodeInput,
                    onValueChange = onBincodeInputChange,
                    label = { Text("Texto para Serializar (Bincode format)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF87),
                        unfocusedBorderColor = Color(0xFF2E384E),
                        focusedLabelColor = Color(0xFF00FF87),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        unfocusedLabelColor = Color.Gray
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bincode_input_field")
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF07090C))
                        .border(1.dp, Color(0xFF1E2638))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "BINARY STREAM HEX (u64 len LE + raw UTF-8):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF00FF87),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = soxcimaMemory.bincodeHexOutput,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF00FF87),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bincode_hex_output")
                    )
                }
            }
        }
    }
}

@Composable
fun borderStrokeForNote(cifrada: Boolean): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = if (cifrada) Color(0xFF00FF87).copy(alpha = 0.3f) else Color(0xFF2E384E)
    )
}

@Composable
fun BridgeTabContent(
    config: com.example.data.SoxcimaConfigEntity,
    stagedTx: StagedTx?,
    generatedInvitationQr: String?,
    invitationValidationResult: String?,
    sovereignState: SovereignCycleState,
    isLightMode: Boolean,
    onToggleBridge: (Boolean, String?) -> Unit,
    onToggleAccesoMundial: (Boolean) -> Unit,
    onApproveTx: () -> Unit,
    onClearTx: () -> Unit,
    onGenerarInvitacion: (Long) -> Unit,
    onValidarInvitacion: (String) -> Unit,
    onClearValidationResult: () -> Unit,
    onClearGeneratedInvitation: () -> Unit,
    onToggleSovereign: (Boolean) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var destinationInput by remember { mutableStateOf(config.destinationAllowed ?: "") }

    val bgCard = if (isLightMode) Color(0xFFFFFFFF) else Color(0xFF161A23)
    val bgSubCard = if (isLightMode) Color(0xFFF3F4F6) else Color(0xFF0F1115)
    val txtMain = if (isLightMode) Color(0xFF111827) else Color.White
    val txtSub = if (isLightMode) Color(0xFF4B5563) else Color.Gray
    val borderCol = if (isLightMode) Color(0xFFD1D5DB) else Color(0xFF2E384E)
    val dividerCol = if (isLightMode) Color(0xFFE5E7EB) else Color(0xFF2E384E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bridge Switch Control Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = bgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Power,
                            contentDescription = null,
                            tint = if (config.bridgeActive) Color(0xFF00FF87) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "NODO PUENTE SOXCIMA",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = txtMain
                                )
                            )
                            Text(
                                text = "Permiso de compartición de red",
                                style = MaterialTheme.typography.labelSmall.copy(color = txtSub)
                            )
                        }
                    }

                    Switch(
                        checked = config.bridgeActive,
                        onCheckedChange = { active ->
                            val dest = if (destinationInput.isBlank()) null else destinationInput
                            onToggleBridge(active, dest)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00FF87),
                            checkedTrackColor = Color(0xFF00FF87).copy(alpha = 0.3f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("bridge_active_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFF2E384E))

                Text(
                    text = "Configuración del puente",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )

                OutlinedTextField(
                    value = destinationInput,
                    onValueChange = { destinationInput = it },
                    label = { Text("Firma Única del Nodo Permitido") },
                    placeholder = { Text("Ej: SOCXIMA-NODE-VINA-DEL-MAR") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF87),
                        unfocusedBorderColor = Color(0xFF2E384E),
                        focusedLabelColor = Color(0xFF00FF87),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("bridge_destination_input")
                )

                Button(
                    onClick = {
                        val dest = if (destinationInput.isBlank()) null else destinationInput
                        onToggleBridge(config.bridgeActive, dest)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638), contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().testTag("apply_bridge_config_button")
                ) {
                    Text("APLICAR CONFIGURACIÓN DE PUENTE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }

                // === ESTADO DEL PUENTE (ESTILO EGUI RUST) ===
                val puenteActivo = config.bridgeActive
                val puenteCualquiera = config.destinationAllowed.isNullOrBlank()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(10, 30, 25, 220)) // Color32::from_rgba_unmultiplied(10, 30, 25, 220)
                        .border(1.dp, Color(0xFF00C88C), RoundedCornerShape(8.dp)) // Stroke Color32::from_rgb(0, 200, 140)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val iconoActivo = if (puenteActivo) "✅" else "❌"
                            Text(
                                text = "$iconoActivo PUENTE ACTIVADO POR TI: SOLO comparte internet",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green
                                )
                            )
                        }

                        Text(
                            text = "con ${config.destinationAllowed ?: "CUALQUIER NODO"}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val iconoCualquiera = if (!puenteCualquiera) "✅" else "❌"
                            Text(
                                text = "$iconoCualquiera NO se comparte con ningún otro nodo.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                            )
                        }
                    }
                }
            }
        }

        // === INTERRUPTOR NÚCLEO SOBERANO / ACCESO MUNDIAL ===
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isLightMode) Color(0xFFEAF9F5) else Color(10, 25, 20, 220)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isLightMode) Color(0xFF00CC88) else Color(0xFF00FFAA)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sovereign_bridge_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔌 NÚCLEO SOBERANO",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isLightMode) Color(0xFF006644) else Color(0xFF00FFAA)
                            )
                        )
                    }

                    Switch(
                        checked = config.accesoMundialAbierto,
                        onCheckedChange = { active ->
                            onToggleAccesoMundial(active)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00FFAA),
                            checkedTrackColor = Color(0xFF00FFAA).copy(alpha = 0.3f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("acceso_mundial_switch")
                    )
                }

                Text(
                    text = "Ciclo Maestro de Dominio Total — Acceso mundial abierto",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = txtMain
                    )
                )

                val statusText = if (config.accesoMundialAbierto) {
                    "🌐 PUENTE MUNDIAL ABIERTO ACTIVADO\n" +
                    "✅ Cualquier nodo SOXCIMA del mundo puede conectarse\n" +
                    "🔒 Todo tráfico cifrado extremo a extremo\n" +
                    "⚠️ Tú puedes desactivarlo en cualquier momento"
                } else {
                    "🛑 PUENTE CERRADO: Ya no hay acceso desde ningún nodo"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (config.accesoMundialAbierto) Color(0xFF07090C) else Color(0xFF1F1212))
                        .border(1.dp, if (config.accesoMundialAbierto) Color(0xFF1E2638) else Color(0xFF381E1E))
                        .padding(12.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = if (config.accesoMundialAbierto) Color(0xFF00FF87) else Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Active Staged Tx Block (enviar_nota_cifrada)
        if (stagedTx != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161A23)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF87)),
                modifier = Modifier.fillMaxWidth().testTag("staged_tx_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRANSMISIÓN STAGED (LISTA)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF87),
                                letterSpacing = 1.sp
                            )
                        )

                        IconButton(
                            onClick = onClearTx,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Staged Tx",
                                tint = Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F1115))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Nota: ${stagedTx.noteTitle}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Destinatario: ${stagedTx.targetNode}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Payload Cifrado Base64:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stagedTx.payload.take(24) + "...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF00FF87)
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(stagedTx.payload)) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Cipher",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Progress / Status indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (stagedTx.status) {
                                        "Success" -> Color(0xFF00FF87)
                                        "Transmitting" -> Color.Cyan
                                        "Error" -> Color.Red
                                        else -> Color(0xFFFFB300)
                                    }
                                )
                        )
                        Text(
                            text = when (stagedTx.status) {
                                "Success" -> "✅ TRANSMITIDO VIA PUENTE SEGURO"
                                "Transmitting" -> "⌛ ENVIANDO PAQUETES..."
                                "Error" -> "❌ FALLÓ: VERIFIQUE PUENTE ACTIVO Y DESTINO"
                                else -> "⚠️ LISTA: NO SE ENVÍA SIN TU APROBACIÓN"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (stagedTx.status) {
                                    "Success" -> Color(0xFF00FF87)
                                    "Error" -> Color.Red
                                    else -> Color.White
                                }
                            )
                        )
                    }

                    if (stagedTx.status != "Success") {
                        Button(
                            onClick = onApproveTx,
                            enabled = stagedTx.status != "Transmitting",
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87), contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("approve_tx_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("APROBAR Y TRANSMITIR P2P", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // === INVITACIONES QR — SOXCIMA INFINITY P2P ===
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161A23)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E384E)),
            modifier = Modifier.fillMaxWidth().testTag("qr_invitation_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = Color(0xFF00FF87),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "INVITACIONES QR P2P",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Firma: SOXCIMA-EVELIO-7A9B2F4C-NUCLEO-INMUTABLE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00FF87),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF2E384E))

                Text(
                    text = "1. Generar invitación QR",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onGenerarInvitacion(86400) }, // 24h
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638), contentColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("generate_24h_invite_btn")
                    ) {
                        Text("24 HORAS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onGenerarInvitacion(300) }, // 5 min for quick test
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638), contentColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("generate_5m_invite_btn")
                    ) {
                        Text("5 MINUTOS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                if (generatedInvitationQr != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SoxcimaQrCode(
                                text = generatedInvitationQr,
                                modifier = Modifier
                                    .size(160.dp)
                                    .testTag("invitation_qr_code_image")
                            )

                            Text(
                                text = "🎫 ESCANEA CON OTRO DISPOSITIVO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF121212),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F1115))
                            .border(1.dp, Color(0xFF1E2638))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Contenido seguro:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                        Text(
                            text = generatedInvitationQr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00FF87)
                            ),
                            lineHeight = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { clipboardManager.setText(AnnotatedString(generatedInvitationQr)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638), contentColor = Color.White),
                                modifier = Modifier.weight(1f).height(32.dp).testTag("copy_generated_invite_btn")
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COPIAR", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = onClearGeneratedInvitation,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1A1A), contentColor = Color.Red),
                                modifier = Modifier.weight(1f).height(32.dp).testTag("clear_generated_invite_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LIMPIAR", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF2E384E))

                Text(
                    text = "2. Validar Invitación QR",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )

                var inviteInputText by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = inviteInputText,
                    onValueChange = { inviteInputText = it },
                    label = { Text("Contenido o Cadena QR") },
                    placeholder = { Text("SOXCIMA://RED/...|SIG=...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF87),
                        unfocusedBorderColor = Color(0xFF2E384E),
                        focusedLabelColor = Color(0xFF00FF87),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("invite_input_field")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (generatedInvitationQr != null) {
                        Button(
                            onClick = { inviteInputText = generatedInvitationQr },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638), contentColor = Color(0xFF00FF87)),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f).height(30.dp).testTag("quick_load_active_invite")
                        ) {
                            Text("PEGAR ACTIVO", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Button(
                        onClick = {
                            val peerId = config.nodeIdentity
                            val datosBase = "SOXCIMA://RED/v7.7/PEER=$peerId/DIR=SOXCIMA-ADDR-ADDR/EXP=1719999999"
                            val hash = java.security.MessageDigest.getInstance("SHA-256")
                                .digest(datosBase.toByteArray(Charsets.UTF_8))
                                .fold("") { str, it -> str + "%02x".format(it) }
                            inviteInputText = "$datosBase|SIG=${hash.take(32)}"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF221A16), contentColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(30.dp).testTag("quick_load_expired_invite")
                    ) {
                        Text("SIMULAR EXPIRADO", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onValidarInvitacion(inviteInputText) },
                        enabled = inviteInputText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87), contentColor = Color.Black),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("validate_invite_btn")
                    ) {
                        Text("VALIDAR INVITACIÓN", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }

                    if (invitationValidationResult != null) {
                        Button(
                            onClick = {
                                onClearValidationResult()
                                inviteInputText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638), contentColor = Color.LightGray),
                            modifier = Modifier.height(38.dp).testTag("clear_validation_result_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                if (invitationValidationResult != null) {
                    val isSuccess = invitationValidationResult.startsWith("✅")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSuccess) Color(0xFF00FF87).copy(alpha = 0.08f) else Color.Red.copy(alpha = 0.08f))
                            .border(1.dp, if (isSuccess) Color(0xFF00FF87).copy(alpha = 0.4f) else Color.Red.copy(alpha = 0.4f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = invitationValidationResult,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSuccess) Color(0xFF00FF87) else Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold
                            ),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // === CICLO MAESTRO DE SOBERANÍA — NÚCLEO INMUTABLE ===
        Card(
            colors = CardDefaults.cardColors(containerColor = bgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            modifier = Modifier.fillMaxWidth().testTag("sovereignty_cycle_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = if (sovereignState.isRunning) Color(0xFF00FF87) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "NÚCLEO SOBERANO",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = txtMain
                                )
                            )
                            Text(
                                text = "Ciclo Maestro de Dominio Total",
                                style = MaterialTheme.typography.labelSmall.copy(color = txtSub)
                            )
                        }
                    }

                    Switch(
                        checked = sovereignState.isRunning,
                        onCheckedChange = { active -> onToggleSovereign(active) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00FF87),
                            checkedTrackColor = Color(0xFF00FF87).copy(alpha = 0.3f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("sovereignty_cycle_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFF2E384E))

                // Estado actual / Mensaje
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (sovereignState.isRunning) Color(0xFF07090C) else Color(0xFF1F1212))
                        .border(1.dp, if (sovereignState.isRunning) Color(0xFF1E2638) else Color(0xFF381E1E))
                        .padding(12.dp)
                ) {
                    Text(
                        text = sovereignState.statusMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = if (sovereignState.isRunning) Color(0xFF00FF87) else Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (sovereignState.isRunning) {
                    Text(
                        text = "FLUJO GENERAL DE EJECUCIÓN SOBERANA:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // 5-Step visual track
                    val stages = listOf(
                        "INGESTA" to "1. Ingesta (Captura de datos)",
                        "PROCESAMIENTO" to "2. Procesamiento (IA Local)",
                        "ACCIÓN" to "3. Acción (Ejecución)",
                        "AUTOCURACIÓN" to "4. Autocuración (Integridad)",
                        "LATIDO" to "5. Latido (Siguiente ciclo)"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stages.forEach { (stageKey, stageLabel) ->
                            val isActive = sovereignState.currentStage == stageKey
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isActive) Color(0xFF00FF87).copy(alpha = 0.08f) else Color.Transparent)
                                    .border(1.dp, if (isActive) Color(0xFF00FF87).copy(alpha = 0.4f) else Color.Transparent)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(if (isActive) Color(0xFF00FF87) else Color.DarkGray)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stageLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isActive) Color.White else Color.Gray,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isActive) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "EJECUTANDO...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF00FF87),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFF2E384E))

                    // Metricas en vivo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ciclos
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgSubCard)
                                .border(1.dp, borderCol)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("CICLOS", style = MaterialTheme.typography.labelSmall.copy(color = txtSub))
                            Text(
                                text = "${sovereignState.cyclesCount}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF87),
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        // Ingesta
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgSubCard)
                                .border(1.dp, borderCol)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("INGESTA", style = MaterialTheme.typography.labelSmall.copy(color = txtSub))
                            Text(
                                text = if (sovereignState.lastIngestValue > 0) "${sovereignState.lastIngestValue}" else "-",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = txtMain,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        // Dominio
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgSubCard)
                                .border(1.dp, borderCol)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("IA DOMINIO", style = MaterialTheme.typography.labelSmall.copy(color = txtSub))
                            Text(
                                text = if (sovereignState.lastIntelligenceLevel > 0) "NIVEL ${sovereignState.lastIntelligenceLevel}" else "-",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = txtMain,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        // Autocuracion
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgSubCard)
                                .border(1.dp, borderCol)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("AUTOCURAR", style = MaterialTheme.typography.labelSmall.copy(color = txtSub))
                            Text(
                                text = sovereignState.lastIntegrityStatus,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (sovereignState.lastIntegrityStatus == "OK (INTACTO)") Color(0xFF00FF87) else txtSub,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Live Combined Telemetry Chart
        SovereignTelemetryChart(
            telemetryHistory = sovereignState.telemetryHistory,
            isLightMode = isLightMode,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Bridge raw JSON State output (representing Rust estado() method)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "ESTADO() MÓDULO RUST REPRESENTACIÓN",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontFamily = FontFamily.Monospace
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF07090C))
                    .border(1.dp, Color(0xFF1E2638))
                    .padding(14.dp)
            ) {
                Text(
                    text = """
{
  "puente_activo": ${config.bridgeActive},
  "destino_permitido": ${if (config.destinationAllowed != null) "\"${config.destinationAllowed}\"" else "null"},
  "modo": "SOLO con permiso explícito del administrador",
  "origen": "SOXCIMA-PUENTE-SEGURO"
}
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00FF87),
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TerminalTabContent(
    logs: List<String>,
    nodeIdentity: String,
    isLightMode: Boolean,
    onClearLogs: () -> Unit
) {
    val listState = rememberLazyListState()

    // Auto-scroll terminal when a new log appears
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NUCLEO LOG DIAGNÓSTICO",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )

            IconButton(
                onClick = onClearLogs,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                    .size(32.dp)
                    .testTag("clear_terminal_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Clear console",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Monospaced terminal logs block
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isLightMode) Color(0xFFF3F4F6) else Color(0xFF050608))
                .border(1.dp, if (isLightMode) Color(0xFFD1D5DB) else Color(0xFF1A2233))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(logs) { log ->
                val color = when {
                    log.contains("❌") || log.contains("ERROR") -> Color.Red
                    log.contains("✅") || log.contains("SUCCESS") || log.contains("APLICAR") || log.contains("ONLINE") -> Color(0xFF00FF87)
                    log.contains("⚠️") || log.contains("Pendiente") || log.contains("warning") -> Color(0xFFFFB300)
                    else -> Color.LightGray
                }

                Text(
                    text = log,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = color,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Diagnostic Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    onClearLogs()
                    onClearLogs() // triggers dummy updates to simulate active self test
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638), contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("self_test_button")
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AUTO TEST DIAGNÓSTICO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Pseudo-QR code painter custom canvas as designed in analysis
@Composable
fun SoxcimaQrCode(text: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val numBlocks = 15
        val blockSize = sizePx / numBlocks

        // Use a simple deterministic hash of the text to seed a random generator
        val hash = text.hashCode().toLong()
        val random = java.util.Random(hash)

        // Draw white background
        drawRect(color = Color.White)

        // Draw standard QR finder patterns in corners (top-left, top-right, bottom-left)
        val finderSize = 5

        fun drawFinder(offsetX: Int, offsetY: Int) {
            // Outer block
            drawRect(
                color = Color.Black,
                topLeft = androidx.compose.ui.geometry.Offset(offsetX * blockSize, offsetY * blockSize),
                size = androidx.compose.ui.geometry.Size(finderSize * blockSize, finderSize * blockSize)
            )
            // Inner white
            drawRect(
                color = Color.White,
                topLeft = androidx.compose.ui.geometry.Offset((offsetX + 1) * blockSize, (offsetY + 1) * blockSize),
                size = androidx.compose.ui.geometry.Size((finderSize - 2) * blockSize, (finderSize - 2) * blockSize)
            )
            // Inner black center
            drawRect(
                color = Color.Black,
                topLeft = androidx.compose.ui.geometry.Offset((offsetX + 2) * blockSize, (offsetY + 2) * blockSize),
                size = androidx.compose.ui.geometry.Size((finderSize - 4) * blockSize, (finderSize - 4) * blockSize)
            )
        }

        // Corner patterns
        drawFinder(0, 0)
        drawFinder(numBlocks - finderSize, 0)
        drawFinder(0, numBlocks - finderSize)

        // Fill the rest with pseudo-random blocks
        for (x in 0 until numBlocks) {
            for (y in 0 until numBlocks) {
                // Skip finder pattern zones
                val inTopLeftFinder = x < finderSize && y < finderSize
                val inTopRightFinder = x >= numBlocks - finderSize && y < finderSize
                val inBottomLeftFinder = x < finderSize && y >= numBlocks - finderSize

                if (!inTopLeftFinder && !inTopRightFinder && !inBottomLeftFinder) {
                    val isBlack = random.nextBoolean()
                    if (isBlack) {
                        drawRect(
                            color = Color.Black,
                            topLeft = androidx.compose.ui.geometry.Offset(x * blockSize, y * blockSize),
                            size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SovereignTelemetryChart(
    telemetryHistory: List<com.example.ui.TelemetryEntry>,
    isLightMode: Boolean,
    modifier: Modifier = Modifier
) {
    val neonGreen = Color(0xFF00FF87)
    val neonPurple = Color(0xFF9D4EDD)
    val gridColor = if (isLightMode) Color(0xFFD1D5DB) else Color(0xFF2E384E)
    val textColor = if (isLightMode) Color(0xFF4B5563) else Color.Gray

    Card(
        colors = CardDefaults.cardColors(containerColor = if (isLightMode) Color.White else Color(0xFF161A23)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isLightMode) Color(0xFFE2E8F0) else Color(0xFF2E384E)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TELEMETRÍA COMBINADA EN TIEM REAL",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isLightMode) Color(0xFF1F2937) else Color.White,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "Gráfico unificado de Ingesta (Verde) e Inteligencia Local (Púrpura)",
                style = MaterialTheme.typography.labelSmall.copy(color = textColor),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(neonGreen, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ingesta (Bytes)", style = MaterialTheme.typography.labelSmall.copy(color = if (isLightMode) Color(0xFF374151) else Color.LightGray))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(neonPurple, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Inteligencia (Nivel)", style = MaterialTheme.typography.labelSmall.copy(color = if (isLightMode) Color(0xFF374151) else Color.LightGray))
                }
            }

            // Chart area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(if (isLightMode) Color(0xFFF9FAFB) else Color(0xFF0F1115), RoundedCornerShape(4.dp))
                    .border(1.dp, gridColor, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Draw 4 horizontal grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = (height / gridLines) * i
                        drawLine(
                            color = gridColor.copy(alpha = 0.4f),
                            start = androidx.compose.ui.geometry.Offset(0f, y),
                            end = androidx.compose.ui.geometry.Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Draw 4 vertical grid lines
                    val vGridLines = 5
                    for (i in 0..vGridLines) {
                        val x = (width / vGridLines) * i
                        drawLine(
                            color = gridColor.copy(alpha = 0.4f),
                            start = androidx.compose.ui.geometry.Offset(x, 0f),
                            end = androidx.compose.ui.geometry.Offset(x, height),
                            strokeWidth = 1f
                        )
                    }

                    if (telemetryHistory.size > 1) {
                        val maxCycleValue = telemetryHistory.maxOfOrNull { maxOf(it.ingest, it.intelligence) } ?: 300
                        val maxVal = maxOf(maxCycleValue.toFloat(), 100f) * 1.1f // 10% padding on top

                        val pointsIngest = mutableListOf<androidx.compose.ui.geometry.Offset>()
                        val pointsIntel = mutableListOf<androidx.compose.ui.geometry.Offset>()

                        val stepX = width / (telemetryHistory.size - 1)

                        telemetryHistory.forEachIndexed { index, entry ->
                            val x = index * stepX
                            
                            // Map ingest (0..maxVal) to (height..0)
                            val yIngest = height - (entry.ingest.toFloat() / maxVal) * height
                            pointsIngest.add(androidx.compose.ui.geometry.Offset(x, yIngest))

                            // Map intelligence
                            val yIntel = height - (entry.intelligence.toFloat() / maxVal) * height
                            pointsIntel.add(androidx.compose.ui.geometry.Offset(x, yIntel))
                        }

                        // Draw Ingest path (neonGreen)
                        val ingestPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(pointsIngest[0].x, pointsIngest[0].y)
                            for (i in 1 until pointsIngest.size) {
                                lineTo(pointsIngest[i].x, pointsIngest[i].y)
                            }
                        }
                        drawPath(
                            path = ingestPath,
                            color = neonGreen,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                join = androidx.compose.ui.graphics.StrokeJoin.Round
                            )
                        )

                        // Draw Intel path (neonPurple)
                        val intelPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(pointsIntel[0].x, pointsIntel[0].y)
                            for (i in 1 until pointsIntel.size) {
                                lineTo(pointsIntel[i].x, pointsIntel[i].y)
                            }
                        }
                        drawPath(
                            path = intelPath,
                            color = neonPurple,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                join = androidx.compose.ui.graphics.StrokeJoin.Round
                            )
                        )

                        // Draw circular markers at each point
                        pointsIngest.forEach { offset ->
                            drawCircle(color = neonGreen, radius = 4.dp.toPx(), center = offset)
                            drawCircle(color = if (isLightMode) Color.White else Color(0xFF0F1115), radius = 2.dp.toPx(), center = offset)
                        }

                        pointsIntel.forEach { offset ->
                            drawCircle(color = neonPurple, radius = 4.dp.toPx(), center = offset)
                            drawCircle(color = if (isLightMode) Color.White else Color(0xFF0F1115), radius = 2.dp.toPx(), center = offset)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatTabContent(
    chatMessages: List<com.example.ui.SoxcimaChatMessage>,
    isLightMode: Boolean,
    onSendMessage: (String) -> Unit
) {
    val listState = rememberLazyListState()
    var textInput by remember { mutableStateOf("") }

    val bgCard = if (isLightMode) Color(0xFFFFFFFF) else Color(0xFF161A23)
    val bgInput = if (isLightMode) Color(0xFFF3F4F6) else Color(0xFF0F1115)
    val txtMain = if (isLightMode) Color(0xFF111827) else Color.White
    val txtSub = if (isLightMode) Color(0xFF4B5563) else Color.Gray
    val borderCol = if (isLightMode) Color(0xFFD1D5DB) else Color(0xFF2E384E)

    // Automatically scroll to the last message when list changes size
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Chat Header
        Card(
            colors = CardDefaults.cardColors(containerColor = bgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CANAL DE COMUNICACIÓN P2P",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isLightMode) Color(0xFF008A47) else Color(0xFF00FF87),
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "Código Abierto • Cifrado Curva25519/ChaCha20",
                        style = MaterialTheme.typography.labelSmall.copy(color = txtSub)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF00FF87).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CONEXIÓN SEGURA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF87),
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }

        // Messages Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isLightMode) Color(0xFFF9FAFB) else Color(0xFF050608))
                .border(1.dp, borderCol)
                .padding(8.dp)
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chatMessages) { msg ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                    ) {
                        // Sender ID
                        Text(
                            text = msg.sender,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isUser) Color(0xFF00FF87) else (if (isLightMode) Color(0xFF1F2937) else Color.LightGray),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        // Message bubble
                        val bubbleColor = if (msg.isUser) {
                            if (isLightMode) Color(0xFFDCFCE7) else Color(0xFF1E3A24)
                        } else {
                            if (isLightMode) Color(0xFFE5E7EB) else Color(0xFF1F2937)
                        }

                        val bubbleBorder = if (msg.isUser) {
                            if (isLightMode) Color(0xFF86EFAC) else Color(0xFF00FF87).copy(alpha = 0.5f)
                        } else {
                            borderCol
                        }

                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (msg.isUser) 12.dp else 0.dp,
                                        bottomEnd = if (msg.isUser) 0.dp else 12.dp
                                    )
                                )
                                .background(bubbleColor)
                                .border(
                                    1.dp,
                                    bubbleBorder,
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (msg.isUser) 12.dp else 0.dp,
                                        bottomEnd = if (msg.isUser) 0.dp else 12.dp
                                    )
                                )
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (msg.isUser && isLightMode) Color(0xFF065F46) else txtMain
                                    )
                                )

                                if (msg.isEncrypted && msg.encryptedPayloadHex.isNotEmpty()) {
                                    HorizontalDivider(
                                        color = if (msg.isUser) Color(0xFF00FF87).copy(alpha = 0.3f) else borderCol,
                                        thickness = 0.5.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    Text(
                                        text = "Payload Cifrado:\n${msg.encryptedPayloadHex}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (msg.isUser) Color(0xFF00FF87).copy(alpha = 0.8f) else Color.Magenta,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            lineHeight = 11.sp
                                        )
                                    )
                                    Text(
                                        text = "Paquete de Red: ${msg.packetSize} Bytes",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = txtSub,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Time
                        Text(
                            text = msg.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = txtSub,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Message Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        "Escribe un mensaje de código abierto...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = txtSub)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = txtMain,
                    unfocusedTextColor = txtMain,
                    focusedContainerColor = bgInput,
                    unfocusedContainerColor = bgInput,
                    focusedBorderColor = Color(0xFF00FF87),
                    unfocusedBorderColor = borderCol
                ),
                maxLines = 3
            )

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF00FF87))
                    .size(48.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar Mensaje",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun WebTabContent(
    isLightMode: Boolean,
    config: com.example.data.SoxcimaConfigEntity,
    sovereignState: com.example.ui.SovereignCycleState
) {
    val scrollState = rememberScrollState()

    val bgCard = if (isLightMode) Color(0xFFFFFFFF) else Color(0xFF161A23)
    val bgInput = if (isLightMode) Color(0xFFF3F4F6) else Color(0xFF0F1115)
    val txtMain = if (isLightMode) Color(0xFF111827) else Color.White
    val txtSub = if (isLightMode) Color(0xFF4B5563) else Color.Gray
    val borderCol = if (isLightMode) Color(0xFFD1D5DB) else Color(0xFF2E384E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Web Hero banner
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isLightMode) Color(0xFF008A47) else Color(0xFF111827)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SITIO WEB OFICIAL EN LÍNEA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }

                Text(
                    text = "soxcima.org",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Text(
                    text = "El sistema operativo descentralizado definitivo para la autogestión de datos soberanos e inteligencia colectiva en red local.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f))
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Licencia: Apache 2.0 / MIT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "V7.4-STABLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF00FF87),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        // Section 1: Características Clave del Protocolo
        Text(
            text = "ESPECIFICACIONES DEL PROTOCOLO",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isLightMode) Color(0xFF1F2937) else Color.White,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = bgCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sled Embedded DB",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = txtMain)
                    )
                    Text(
                        text = "Base de datos transaccional integrada escrita en Rust. Lock-free y de ultra alta velocidad.",
                        style = MaterialTheme.typography.labelSmall.copy(color = txtSub)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = bgCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Bincode Encoding",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = txtMain)
                    )
                    Text(
                        text = "Serialización binaria compacta y de tipado estricto que reduce el overhead de red al 12%.",
                        style = MaterialTheme.typography.labelSmall.copy(color = txtSub)
                    )
                }
            }
        }

        // Section 2: Arquitectura Descentralizada Real
        Card(
            colors = CardDefaults.cardColors(containerColor = bgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ESTRUCTURA DEL PAQUETE ABIERTO SOXCIMA",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = txtMain, fontFamily = FontFamily.Monospace)
                )

                Text(
                    text = "El núcleo lee y empaqueta estructuras inmutables bajo el siguiente formato de código abierto Rust:",
                    style = MaterialTheme.typography.labelMedium.copy(color = txtSub)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgInput)
                        .padding(12.dp)
                ) {
                    Text(
                        text = """
#[derive(Serialize, Deserialize, Debug)]
pub struct SoxcimaPacket<T> {
    pub version: u8,
    pub timestamp: u64,
    pub sender: String,
    pub payload: Vec<u8>,
    pub signature: [u8; 64],
    pub metadata: T,
}

impl<T> SoxcimaPacket<T> {
    pub fun sign(&mut self, kp: &Keypair) -> Result<(), Error>;
    pub fun verify(&self) -> bool;
}
                        """.trimIndent(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isLightMode) Color(0xFF0F172A) else Color(0xFF00FF87),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    )
                }
            }
        }

        // Section 3: Global Network Status (Visual Node Map Representation)
        Card(
            colors = CardDefaults.cardColors(containerColor = bgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ESTADO GLOBAL DE LA RED SOXCIMA",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = txtMain, fontFamily = FontFamily.Monospace)
                )

                Text(
                    text = "Simulación en tiempo real de nodos interconectados mundialmente:",
                    style = MaterialTheme.typography.labelMedium.copy(color = txtSub)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(bgInput, RoundedCornerShape(4.dp))
                        .border(1.dp, borderCol, RoundedCornerShape(4.dp))
                ) {
                    // Let's draw map nodes dynamically using a Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Coordinates for nodes (Europe, US, Latam, Asia)
                        val nodesList = listOf(
                            androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.3f), // US East
                            androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.7f), // Latam
                            androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.25f), // Europe
                            androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.4f), // Asia
                            androidx.compose.ui.geometry.Offset(w * 0.75f, h * 0.75f) // Australia
                        )

                        // Draw connection lines
                        for (i in nodesList.indices) {
                            for (j in (i + 1) until nodesList.size) {
                                drawLine(
                                    color = Color(0xFF00FF87).copy(alpha = 0.35f),
                                    start = nodesList[i],
                                    end = nodesList[j],
                                    strokeWidth = 2f
                                )
                            }
                        }

                        // Draw node points
                        nodesList.forEachIndexed { idx, offset ->
                            val color = if (idx == 1) Color(0xFF00FF87) else Color(0xFF9D4EDD)
                            drawCircle(color = color, radius = 6.dp.toPx(), center = offset)
                            drawCircle(color = if (isLightMode) Color.White else Color(0xFF0F1115), radius = 3.dp.toPx(), center = offset)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Nodos: 1,482",
                        style = MaterialTheme.typography.labelSmall.copy(color = txtSub, fontFamily = FontFamily.Monospace)
                    )
                    Text(
                        text = "Tasa Consenso: 99.8%",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FF87), fontFamily = FontFamily.Monospace)
                    )
                    Text(
                        text = "P2P Bridge Latency: 14ms",
                        style = MaterialTheme.typography.labelSmall.copy(color = txtSub, fontFamily = FontFamily.Monospace)
                    )
                }
            }
        }

        // Section 4: Downloads and GitHub Link Representation
        Card(
            colors = CardDefaults.cardColors(containerColor = bgCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CÓDIGO FUENTE ABIERTO",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = txtMain)
                    )
                    Text(
                        text = "Audita el código, reporta issues, o clona el repositorio del proyecto Soxcima en GitHub de manera libre.",
                        style = MaterialTheme.typography.labelSmall.copy(color = txtSub)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { /* Simulated download */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF87)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "GITHUB",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
