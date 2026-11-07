package com.example.omnivault

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.omnivault.data.repository.VaultRepository
import com.example.omnivault.data.service.GeminiApiService
import com.example.omnivault.data.service.TtsManager
import com.example.omnivault.ui.screens.ArchitectureScreen
import com.example.omnivault.ui.screens.ChatScreen
import com.example.omnivault.ui.screens.HomeScreen
import com.example.omnivault.ui.theme.CyanPrimary
import com.example.omnivault.ui.theme.Slate400
import com.example.omnivault.ui.theme.Slate800
import com.example.omnivault.ui.theme.Slate900
import com.example.omnivault.ui.theme.Slate950
import com.example.omnivault.ui.viewmodel.ChatViewModel
import com.example.omnivault.ui.viewmodel.VaultViewModel

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun OmniVaultApp() {
    val context = LocalContext.current

    val repository = remember { VaultRepository() }
    val geminiApiService = remember { GeminiApiService() }
    val ttsManager = remember { TtsManager(context) }

    val vaultViewModel: VaultViewModel = remember {
        VaultViewModel(repository, geminiApiService, ttsManager)
    }
    val chatViewModel: ChatViewModel = remember {
        ChatViewModel(geminiApiService)
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        NavItem("My Drive", Icons.Default.CloudDone, "nav_drive_tab"),
        NavItem("AI Assistant", Icons.Default.AutoAwesome, "nav_assistant_tab"),
        NavItem("Cloud Arch", Icons.Default.Storage, "nav_arch_tab")
    )

    BackHandler(enabled = selectedTabIndex != 0) {
        selectedTabIndex = 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Slate950,
        bottomBar = {
            NavigationBar(
                containerColor = Slate900,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) CyanPrimary else Slate400
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (isSelected) CyanPrimary else Slate400,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Slate800
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> HomeScreen(
                viewModel = vaultViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> ChatScreen(
                chatViewModel = chatViewModel,
                vaultViewModel = vaultViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> ArchitectureScreen(
                repository = repository,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
