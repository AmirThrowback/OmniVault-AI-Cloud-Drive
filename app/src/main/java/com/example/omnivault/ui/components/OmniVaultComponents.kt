package com.example.omnivault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omnivault.data.model.FileCategory
import com.example.omnivault.data.model.StorageStats
import com.example.omnivault.data.model.VaultFile
import com.example.omnivault.ui.theme.AmberWarning
import com.example.omnivault.ui.theme.CardBackground
import com.example.omnivault.ui.theme.CardBorder
import com.example.omnivault.ui.theme.CyanPrimary
import com.example.omnivault.ui.theme.EmeraldGreen
import com.example.omnivault.ui.theme.IndigoSecondary
import com.example.omnivault.ui.theme.RoseError
import com.example.omnivault.ui.theme.Slate400
import com.example.omnivault.ui.theme.Slate700
import com.example.omnivault.ui.theme.Slate800
import com.example.omnivault.ui.theme.VioletAccent

@Composable
fun StorageUsageBar(
    stats: StorageStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("storage_usage_card"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Storage Used",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Text(
                    text = "${stats.formattedUsed} / 15 GB",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CyanPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { stats.percentageUsed.coerceIn(0.01f, 100f) / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = CyanPrimary,
                trackColor = Slate800,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBadge(label = "Docs", color = IndigoSecondary)
                StatBadge(label = "Photos", color = EmeraldGreen)
                StatBadge(label = "Notes", color = AmberWarning)
                StatBadge(label = "Vault", color = VioletAccent)
            }
        }
    }
}

@Composable
fun StatBadge(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Slate400
        )
    }
}

@Composable
fun CategoryFilterChips(
    selectedCategory: FileCategory,
    onCategorySelected: (FileCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FileCategory.values().forEach { category ->
            val isSelected = (category == selectedCategory)
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(category.label, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Slate800,
                    labelColor = Color.White,
                    selectedContainerColor = CyanPrimary,
                    selectedLabelColor = Color.Black
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = CardBorder,
                    selectedBorderColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_chip_${category.name.lowercase()}")
            )
        }
    }
}

@Composable
fun FileItemCard(
    file: VaultFile,
    isVaultUnlocked: Boolean,
    isTtsActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLockedItem = file.isEncrypted && !isVaultUnlocked

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("file_card_${file.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLockedItem) VioletAccent.copy(alpha = 0.5f) else CardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            isLockedItem -> VioletAccent.copy(alpha = 0.15f)
                            file.category == FileCategory.IMAGES -> EmeraldGreen.copy(alpha = 0.15f)
                            file.category == FileCategory.NOTES -> AmberWarning.copy(alpha = 0.15f)
                            else -> IndigoSecondary.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isLockedItem -> Icons.Default.Lock
                        file.category == FileCategory.IMAGES -> Icons.Default.Image
                        file.category == FileCategory.NOTES -> Icons.Default.Note
                        file.isEncrypted -> Icons.Default.Key
                        else -> Icons.Default.Description
                    },
                    contentDescription = file.name,
                    tint = when {
                        isLockedItem -> VioletAccent
                        file.category == FileCategory.IMAGES -> EmeraldGreen
                        file.category == FileCategory.NOTES -> AmberWarning
                        else -> IndigoSecondary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isLockedItem) "Encrypted Vault Item" else file.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (file.isEncrypted) {
                        Icon(
                            imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Vault protection",
                            tint = if (isVaultUnlocked) EmeraldGreen else VioletAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (isTtsActive) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "TTS Active",
                            tint = CyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = file.formattedSize,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )

                    Text(
                        text = file.documentType ?: file.category.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (file.documentType != null) CyanPrimary else Slate400,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (file.tags.isNotEmpty() && !isLockedItem) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        file.tags.take(3).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Slate800)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = Slate400
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaultLockBanner(
    isVaultUnlocked: Boolean,
    onUnlockClick: () -> Unit,
    onLockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vault_status_banner"),
        colors = CardDefaults.cardColors(
            containerColor = if (isVaultUnlocked) EmeraldGreen.copy(alpha = 0.1f) else VioletAccent.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isVaultUnlocked) EmeraldGreen.copy(alpha = 0.4f) else VioletAccent.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isVaultUnlocked) EmeraldGreen else VioletAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isVaultUnlocked) "Encrypted Vault: Unlocked" else "Encrypted Vault: Armed",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isVaultUnlocked) "PIN authenticated. AES-256 items readable." else "Tap Unlock to access protected documents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                }
            }

            Button(
                onClick = if (isVaultUnlocked) onLockClick else onUnlockClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVaultUnlocked) Slate800 else VioletAccent
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("vault_toggle_button")
            ) {
                Text(
                    text = if (isVaultUnlocked) "Lock" else "Unlock",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
