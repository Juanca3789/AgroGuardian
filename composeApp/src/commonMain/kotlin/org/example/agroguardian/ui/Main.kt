package org.example.agroguardian.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.example.agroguardian.data.state.NavigationScreen
import org.example.agroguardian.data.state.SpecialRoutes
import org.example.agroguardian.theme.AgroGuardianTheme
import org.example.agroguardian.ui.screen.HomeScreen
import org.example.agroguardian.ui.screen.WeatherAndWaterScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    var selectedScreen by remember {
        mutableStateOf<NavigationScreen>(NavigationScreen.Home)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(selectedScreen.name)
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
                }
            )
        },
        floatingActionButton = {
            if(drawerState.isClosed){
                FloatingActionButton(
                    onClick = {
                        navController.navigate(
                            route = SpecialRoutes.chatbot
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assistant,
                            contentDescription = "Asistente Agrónomo"
                        )
                        Spacer(Modifier.size(4.dp))
                        Text("Asistente")
                    }
                }
            }
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
                            selected = (selectedScreen === screen),
                            onClick = {
                                selectedScreen = screen
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate(selectedScreen.name)
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

                            }
                            NavigationScreen.CropGuardian -> {

                            }
                            NavigationScreen.CropsPlanning -> {

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