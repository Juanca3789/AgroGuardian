package com.dev.jcctech.agroguardian.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.jcctech.agroguardian.data.state.NavigationScreen
import com.dev.jcctech.agroguardian.data.state.SpecialRoutes
import com.dev.jcctech.agroguardian.ui.screen.HomeScreen
import com.dev.jcctech.agroguardian.ui.screen.WeatherAndWaterScreen
import com.dev.jcctech.agroguardian.ui.screen.agribrain.AgriBrainScreen
import com.dev.jcctech.agroguardian.ui.screen.cropguardian.CropGuardianScreen
import com.dev.jcctech.agroguardian.ui.screen.forum.ForumNavigation
import com.dev.jcctech.agroguardian.ui.theme.AgroGuardianTheme
import kotlinx.coroutines.launch

val navigationScreens: List<NavigationScreen> = listOf(
    NavigationScreen.Home,
    NavigationScreen.CropGuardian,
    NavigationScreen.CropsPlanning,
    NavigationScreen.PestsAndDiseases,
    NavigationScreen.WeatherAndWater,
    NavigationScreen.EconomyAndInventory,
    NavigationScreen.Community,
    NavigationScreen.DataAndAnalytics,
    NavigationScreen.EducationAndMaintenance,
    NavigationScreen.Settings,
    NavigationScreen.GuideAndAbout
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    screens: List<NavigationScreen> = navigationScreens
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = navigationScreens.find { it.name == currentRoute } ?: NavigationScreen.Home

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(currentScreen.name)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if(drawerState.isClosed){
                                    drawerState.open()
                                    return@launch
                                }
                                drawerState.close()
                            }
                        }) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Abrir Menú de opciones"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(
                                route = SpecialRoutes.chatbot
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assistant,
                            contentDescription = "Asistente Agrónomo"
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerState = drawerState,
                    windowInsets = WindowInsets(0)
                ) {
                    screens.forEach { screen ->
                        NavigationDrawerItem(
                            label = {
                                Text(screen.name)
                            },
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = "Icono ${screen.name}")
                            },
                            selected = (currentScreen === screen),
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate(screen.name)
                            }
                        )
                    }
                }
            },
            modifier = Modifier
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationScreen.Home.name
            ) {
                navigationScreens.forEach { navigationScreen ->
                    composable(
                        route = navigationScreen.name
                    ) {
                        when(navigationScreen){
                            NavigationScreen.Community -> {
                                ForumNavigation()
                            }
                            NavigationScreen.CropGuardian -> {
                                CropGuardianScreen()
                            }
                            NavigationScreen.CropsPlanning -> {
                                AgriBrainScreen()
                            }
                            NavigationScreen.DataAndAnalytics -> {

                            }
                            NavigationScreen.EconomyAndInventory -> {

                            }
                            NavigationScreen.EducationAndMaintenance -> {

                            }
                            NavigationScreen.GuideAndAbout -> {

                            }
                            NavigationScreen.Home -> {
                                HomeScreen()
                            }
                            NavigationScreen.PestsAndDiseases -> {

                            }
                            NavigationScreen.Settings -> {

                            }
                            NavigationScreen.WeatherAndWater -> {
                                WeatherAndWaterScreen()
                            }
                        }
                    }
                }
                composable(
                    route = SpecialRoutes.chatbot
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
fun MainPreview() {
    AgroGuardianTheme {
        Main()
    }
}
