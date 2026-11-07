package com.example.omnivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omnivault.ui.components.CategoryFilterChips
import com.example.omnivault.ui.components.FileItemCard
import com.example.omnivault.ui.components.StorageUsageBar
import com.example.omnivault.ui.components.VaultLockBanner
import com.example.omnivault.ui.theme.CardBackground
import com.example.omnivault.ui.theme.CardBorder
import com.example.omnivault.ui.theme.CyanPrimary
import com.example.omnivault.ui.theme.EmeraldGreen
import com.example.omnivault.ui.theme.Slate400
import com.example.omnivault.ui.theme.Slate800
import com.example.omnivault.ui.theme.Slate950
import com.example.omnivault.ui.theme.VioletAccent
import com.example.omnivault.ui.viewmodel.VaultViewModel

@Composable
fun HomeScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val files by viewModel.filteredFiles.collectAsState()
    val stats by viewModel.storageStats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val isTtsSpeaking by viewModel.isTtsSpeaking.collectAsState()
    val activeTtsFileId by viewModel.activeTtsFileId.collectAsState()

    val selectedFile by viewModel.selectedFile.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val showLockDialog by viewModel.showLockDialog.collectAsState()
    val pinError by viewModel.pinError.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(Slate950)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OmniVault",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "AI Cloud Drive & Vault",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate400
                            )
                        }
                    }

                    // Vault lock quick badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isVaultUnlocked) EmeraldGreen.copy(alpha = 0.15f) else Slate800)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isVaultUnlocked) EmeraldGreen else VioletAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isVaultUnlocked) "Unlocked" else "Armed",
                                color = if (isVaultUnlocked) EmeraldGreen else VioletAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Search input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = { Text("Search files, OCR text, or tags…", color = Slate400, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Slate400, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_files_input")
                )
            }

            // Storage quota bar
            item {
                StorageUsageBar(stats = stats)
            }

            // Vault Lock Status Banner
            item {
                VaultLockBanner(
                    isVaultUnlocked = isVaultUnlocked,
                    onUnlockClick = { viewModel.openLockDialog() },
                    onLockClick = { viewModel.lockVault() }
                )
            }

            // Category Filter Chips
            item {
                CategoryFilterChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.onCategorySelect(it) }
                )
            }

            // File items header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Files (${files.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AES-256 Protected",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                }
            }

            // File List
            if (files.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No files match your search",
                                color = Slate400,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(files, key = { it.id }) { file ->
                    FileItemCard(
                        file = file,
                        isVaultUnlocked = isVaultUnlocked,
                        isTtsActive = isTtsSpeaking && activeTtsFileId == file.id,
                        onClick = { viewModel.openFileDetail(file) }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { viewModel.openAddFileDialog() },
            containerColor = CyanPrimary,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_file_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add File", modifier = Modifier.size(24.dp))
        }

        // Dialogs & Sheets
        if (showAddDialog) {
            AddFileDialog(
                onDismiss = { viewModel.closeAddFileDialog() },
                onAddFile = { name, content, category, mime, encrypted, tags ->
                    viewModel.addFile(name, content, category, mime, encrypted, tags)
                }
            )
        }

        if (showLockDialog) {
            VaultLockDialog(
                pinError = pinError,
                onDismiss = { viewModel.closeLockDialog() },
                onSubmitPin = { pin -> viewModel.submitPin(pin) }
            )
        }

        selectedFile?.let { file ->
            FileDetailSheet(
                file = file,
                isAnalyzing = isAnalyzing,
                isTtsSpeaking = isTtsSpeaking,
                onDismiss = { viewModel.closeFileDetail() },
                onDelete = { viewModel.deleteFile(it) },
                onAnalyze = { viewModel.analyzeFileWithAi(it) },
                onPlayTts = { viewModel.playTtsNarration(it) },
                onStopTts = { viewModel.stopTtsNarration() }
            )
        }
    }
}
