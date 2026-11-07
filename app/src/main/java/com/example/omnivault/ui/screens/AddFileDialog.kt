package com.example.omnivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omnivault.data.model.FileCategory
import com.example.omnivault.ui.theme.CardBackground
import com.example.omnivault.ui.theme.CardBorder
import com.example.omnivault.ui.theme.CyanPrimary
import com.example.omnivault.ui.theme.Slate400
import com.example.omnivault.ui.theme.Slate800
import com.example.omnivault.ui.theme.VioletAccent

@Composable
fun AddFileDialog(
    onDismiss: () -> Unit,
    onAddFile: (name: String, content: String, category: FileCategory, mimeType: String, isEncrypted: Boolean, tags: List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FileCategory.DOCUMENTS) }
    var isEncrypted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add File to OmniVault",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf(
                        FileCategory.DOCUMENTS to Icons.Default.Description,
                        FileCategory.NOTES to Icons.Default.Note,
                        FileCategory.IMAGES to Icons.Default.Image
                    )
                    categories.forEach { (cat, icon) ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyanPrimary else Slate800)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.label,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // File Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("File Name", color = Slate400) },
                    placeholder = { Text("e.g. tax_return_2026.pdf", color = Slate400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_file_name_input")
                )

                // Content / Description
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content / Document Text", color = Slate400) },
                    placeholder = { Text("Enter text, receipt details, or notes…", color = Slate400) },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_file_content_input")
                )

                // Tags
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Tags (comma separated)", color = Slate400) },
                    placeholder = { Text("finance, taxes, personal", color = Slate400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = Slate800,
                        unfocusedContainerColor = Slate800
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_file_tags_input")
                )

                // Encryption toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate800)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isEncrypted) VioletAccent else Slate400,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Client AES-256 Vault", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Requires PIN to open", color = Slate400, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = isEncrypted,
                        onCheckedChange = { isEncrypted = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VioletAccent
                        ),
                        modifier = Modifier.testTag("add_file_encrypt_switch")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val tags = tagsText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        val mime = when (selectedCategory) {
                            FileCategory.IMAGES -> "image/jpeg"
                            FileCategory.NOTES -> "text/markdown"
                            else -> "application/pdf"
                        }
                        onAddFile(name, content, selectedCategory, mime, isEncrypted, tags)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_file_submit_button")
            ) {
                Text("Add to Vault", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_file_cancel_button")
            ) {
                Text("Cancel", color = Slate400)
            }
        }
    )
}
