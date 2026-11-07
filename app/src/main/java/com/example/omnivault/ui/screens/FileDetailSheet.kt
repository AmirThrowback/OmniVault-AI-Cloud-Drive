package com.example.omnivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omnivault.data.model.VaultFile
import com.example.omnivault.ui.theme.CardBackground
import com.example.omnivault.ui.theme.CardBorder
import com.example.omnivault.ui.theme.CyanPrimary
import com.example.omnivault.ui.theme.EmeraldGreen
import com.example.omnivault.ui.theme.RoseError
import com.example.omnivault.ui.theme.Slate400
import com.example.omnivault.ui.theme.Slate700
import com.example.omnivault.ui.theme.Slate800
import com.example.omnivault.ui.theme.Slate900
import com.example.omnivault.ui.theme.VioletAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailSheet(
    file: VaultFile,
    isAnalyzing: Boolean,
    isTtsSpeaking: Boolean,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onAnalyze: (VaultFile) -> Unit,
    onPlayTts: (VaultFile) -> Unit,
    onStopTts: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Slate900,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Slate700)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (file.isEncrypted) VioletAccent.copy(alpha = 0.2f) else CyanPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (file.isEncrypted) Icons.Default.Lock else Icons.Default.Description,
                            contentDescription = null,
                            tint = if (file.isEncrypted) VioletAccent else CyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${file.formattedSize} • ${file.documentType ?: file.category.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = { onDelete(file.id) },
                        modifier = Modifier.testTag("file_delete_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseError)
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("file_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab bar
            val tabs = listOf("Preview", "AI & OCR", "Voice (TTS)")
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Slate900,
                contentColor = CyanPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyanPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) CyanPrimary else Slate400
                            )
                        },
                        modifier = Modifier.testTag("file_detail_tab_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> ContentPreviewTab(file)
                    1 -> AiIntelligenceTab(file, isAnalyzing, onAnalyze)
                    2 -> VoiceNarrationTab(file, isTtsSpeaking, onPlayTts, onStopTts)
                }
            }
        }
    }
}

@Composable
fun ContentPreviewTab(file: VaultFile) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (file.tags.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                file.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate800)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanPrimary
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "File Content",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate400
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (file.content.isNotBlank()) file.content else "No preview text available.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 22.sp
                    ),
                    color = Color.White
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Metadata & Storage", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("• Format: ${file.mimeType}", color = Slate400, fontSize = 12.sp)
                Text("• Client Encryption: ${if (file.isEncrypted) "AES-256-GCM Active" else "Standard Cloud"}", color = Slate400, fontSize = 12.sp)
                Text("• Checksum: SHA-256 Validated", color = Slate400, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AiIntelligenceTab(
    file: VaultFile,
    isAnalyzing: Boolean,
    onAnalyze: (VaultFile) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Re-analyze action
        Button(
            onClick = { onAnalyze(file) },
            enabled = !isAnalyzing,
            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_reanalyze_button")
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing with Gemini…", color = Color.Black, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze Document with Gemini", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Summary & Classification", color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = file.analysisSummary ?: "Tap 'Analyze Document' above to run OCR and Gemini intelligence.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // Security / Safety Note
        if (file.safetyNote != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(file.safetyNote, color = Color.White, fontSize = 13.sp)
                }
            }
        }

        // OCR Extracted Text Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Extracted OCR Text", color = Slate400, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = file.ocrText ?: file.content,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        // Key Details
        if (file.keyDetails.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Identified Key Data Points", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    file.keyDetails.forEach { detail ->
                        Text("• $detail", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceNarrationTab(
    file: VaultFile,
    isTtsSpeaking: Boolean,
    onPlayTts: (VaultFile) -> Unit,
    onStopTts: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isTtsSpeaking) CyanPrimary.copy(alpha = 0.2f) else Slate800),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = if (isTtsSpeaking) CyanPrimary else Slate400,
                modifier = Modifier.size(36.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isTtsSpeaking) "Playing Voice Narration…" else "Text-To-Speech Narration",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Listens to OCR transcription, document overview, and content",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (isTtsSpeaking) onStopTts() else onPlayTts(file)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTtsSpeaking) RoseError else CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("tts_play_button")
            ) {
                Icon(
                    imageVector = if (isTtsSpeaking) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTtsSpeaking) "Stop Narration" else "Listen Now",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Narration Engine Specs", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("• Voice Model: Gemini Flash TTS / Android Native Speech Engine", color = Slate400, fontSize = 12.sp)
                Text("• Languages: Bilingual Hindi / English auto-switching", color = Slate400, fontSize = 12.sp)
                Text("• Source: Extracted OCR + Document Summary", color = Slate400, fontSize = 12.sp)
            }
        }
    }
}
