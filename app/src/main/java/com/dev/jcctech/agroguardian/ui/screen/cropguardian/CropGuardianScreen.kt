package com.dev.jcctech.agroguardian.ui.screen.cropguardian

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.jcctech.agroguardian.ui.theme.AgroGuardianTheme

enum class CropGuardianScreens {
    ENTRY,
    CAMERA,
    IMPORT,
    TOKED
}

@Composable
fun CropGuardianScreen() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val showBottomBar = currentRoute != CropGuardianScreens.CAMERA.name

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CropGuardianScreens.ENTRY.name,
            modifier = Modifier.padding(
                top = 0.dp,
                start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            composable(CropGuardianScreens.ENTRY.name) {
                EntryScreen(
                    onCameraClick = {
                        navController.navigate(CropGuardianScreens.CAMERA.name)
                    },
                    onImportClick = {
                        navController.navigate(CropGuardianScreens.IMPORT.name)
                    },
                    onHistoryClick = {
                        navController.navigate(CropGuardianScreens.TOKED.name)
                    }
                )
            }
            composable(CropGuardianScreens.CAMERA.name) {
                CameraScreen(
                    onFrameAnalyzed = {},
                    onCaptureClick = {}
                )
            }
            composable(CropGuardianScreens.IMPORT.name) {
                ImportImageScreen(
                    onSelectedImage = {}
                )
            }
            composable(CropGuardianScreens.TOKED.name) {

            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        CropGuardianScreens.CAMERA,
        CropGuardianScreens.IMPORT,
        CropGuardianScreens.TOKED
    )
    NavigationBar {
        val currentBackStackEntry = navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        CropGuardianScreens.CAMERA -> Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
                        CropGuardianScreens.IMPORT -> Icon(Icons.Outlined.Image, contentDescription = null)
                        CropGuardianScreens.TOKED -> Icon(Icons.Outlined.History, contentDescription = null)
                        else -> {

                        }
                    }
                },
                label = {
                    when (screen) {
                        CropGuardianScreens.CAMERA -> Text("Cámara")
                        CropGuardianScreens.IMPORT -> Text("Galería")
                        CropGuardianScreens.TOKED -> Text("Historial")
                        else -> {}
                    }
                },
                selected = currentDestination == screen.name,
                onClick = {
                    navController.navigate(screen.name) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewCropGuardianScreen(){
    AgroGuardianTheme {
        CropGuardianScreen()
    }
}