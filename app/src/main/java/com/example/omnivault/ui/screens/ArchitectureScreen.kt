package com.example.omnivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.omnivault.data.model.StorageArchitecture
import com.example.omnivault.data.repository.VaultRepository
import com.example.omnivault.ui.theme.AmberWarning
import com.example.omnivault.ui.theme.CardBackground
import com.example.omnivault.ui.theme.CardBorder
import com.example.omnivault.ui.theme.CyanPrimary
import com.example.omnivault.ui.theme.EmeraldGreen
import com.example.omnivault.ui.theme.IndigoSecondary
import com.example.omnivault.ui.theme.RoseError
import com.example.omnivault.ui.theme.Slate400
import com.example.omnivault.ui.theme.Slate800
import com.example.omnivault.ui.theme.Slate900
import com.example.omnivault.ui.theme.Slate950
import com.example.omnivault.ui.theme.VioletAccent
import kotlin.math.roundToInt

@Composable
fun ArchitectureScreen(
    repository: VaultRepository,
    modifier: Modifier = Modifier
) {
    val architectures = remember { repository.getArchitectures() }
    var capacityGb by remember { mutableFloatStateOf(500f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudQueue, contentDescription = null, tint = IndigoSecondary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Cloud Architectures",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hosting & Egress Cost Comparison",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }
            }
        }

        // Cost & Egress Calculator Card
        item {
            CostCalculatorCard(
                capacityGb = capacityGb,
                onCapacityChange = { capacityGb = it }
            )
        }

        item {
            Text(
                text = "Architectural Patterns (${architectures.size})",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        items(architectures, key = { it.id }) { arch ->
            ArchitectureItemCard(arch)
        }
    }
}

@Composable
fun CostCalculatorCard(
    capacityGb: Float,
    onCapacityChange: (Float) -> Unit
) {
    val gb = capacityGb.roundToInt()
    val r2Cost = (gb * 0.015f).coerceAtLeast(0f)
    val s3Cost = (gb * 0.023f) + (gb * 0.5f * 0.09f) // assuming 50% download traffic
    val supabaseCost = 25f + (if (gb > 100) (gb - 100) * 0.021f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cost_calculator_card"),
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
                Text("Storage Capacity Slider", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("$gb GB", color = CyanPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = capacityGb,
                onValueChange = onCapacityChange,
                valueRange = 50f..2000f,
                colors = SliderDefaults.colors(
                    thumbColor = CyanPrimary,
                    activeTrackColor = CyanPrimary,
                    inactiveTrackColor = Slate800
                ),
                modifier = Modifier.testTag("storage_capacity_slider")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Estimated Monthly Cost (Storage + Egress):", color = Slate400, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CostPill(
                    provider = "Cloudflare R2",
                    cost = "$${"%.2f".format(r2Cost)}/mo",
                    highlight = "Zero Egress",
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                CostPill(
                    provider = "AWS S3",
                    cost = "$${"%.2f".format(s3Cost)}/mo",
                    highlight = "+ Bandwidth",
                    color = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
                CostPill(
                    provider = "Self-Hosted",
                    cost = "$0.00/mo",
                    highlight = "Hardware only",
                    color = CyanPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CostPill(
    provider: String,
    cost: String,
    highlight: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Slate900)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(provider, color = Slate400, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(cost, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(highlight, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ArchitectureItemCard(arch: StorageArchitecture) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("arch_card_${arch.id}"),
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = arch.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = arch.pricingModel,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate800)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(arch.badge, color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = arch.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                lineHeight = 18.sp
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // Pros
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Advantages:", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    arch.pros.forEach { pro ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(pro, color = Slate400, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Technical Specs
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate800)
                        .padding(10.dp)
                ) {
                    Text(arch.technicalSpecs, color = Color.White, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (expanded) "Show Less" else "View Details & Specs",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }
        }
    }
}
