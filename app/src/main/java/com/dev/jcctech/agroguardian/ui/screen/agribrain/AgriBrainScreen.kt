package com.dev.jcctech.agroguardian.ui.screen.agribrain

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.jcctech.agroguardian.ui.theme.AgroGuardianTheme

enum class AgriBrainScreens {
    PROYECTION,
    ACTUALITY,
    PARCEL_FORM,
    CROP_FORM,
    CHAT
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriBrainScreen() {
    val navController = rememberNavController()
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == AgriBrainScreens.PROYECTION.name,
                    onClick = { navController.navigate(AgriBrainScreens.PROYECTION.name) },
                    label = { Text("Proyección") },
                    icon = { Icon(Icons.Default.Spa, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == AgriBrainScreens.ACTUALITY.name,
                    onClick = { navController.navigate(AgriBrainScreens.ACTUALITY.name) },
                    label = { Text("Parcela") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) }
                )
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (fabExpanded) {
                    ExtendedFloatingActionButton(
                        text = { Text("Registrar cultivo") },
                        icon = { Icon(Icons.Default.Spa, contentDescription = null) },
                        onClick = {
                            fabExpanded = false

                        }
                    )
                    ExtendedFloatingActionButton(
                        text = { Text("Chat IA") },
                        icon = { Icon(Icons.Default.Spa, contentDescription = null) },
                        onClick = {
                            fabExpanded = false
                            navController.navigate(AgriBrainScreens.CHAT.name)
                        }
                    )

                    ExtendedFloatingActionButton(
                        text = { Text("Registrar práctica") },
                        icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            fabExpanded = false

                        }
                    )
                }
                FloatingActionButton(onClick = { fabExpanded = !fabExpanded }) {
                    Icon(Icons.Default.Add, contentDescription = "Acciones")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AgriBrainScreens.PROYECTION.name,
            modifier = Modifier.padding(
                top = 0.dp,
                start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            composable(AgriBrainScreens.PROYECTION.name) {

            }
            composable(AgriBrainScreens.ACTUALITY.name) {

            }
            composable(AgriBrainScreens.CROP_FORM.name) {

            }
            composable(AgriBrainScreens.PARCEL_FORM) {

            }

            // ESTA ES LA NUEVA RUTA
            composable(AgriBrainScreens.CHAT.name) {
                AgriBrainChatScreen()
            }
        }

    }
}

@Preview
@Composable
fun PreviewAgriBrainScreen(){
    AgroGuardianTheme {
        AgriBrainScreen()
    }
}