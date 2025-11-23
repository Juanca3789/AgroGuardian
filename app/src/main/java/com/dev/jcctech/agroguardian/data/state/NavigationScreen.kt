package com.dev.jcctech.agroguardian.data.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface NavigationScreen {
    val icon: ImageVector
    val name: String
    object Home : NavigationScreen {
        override val icon: ImageVector = Icons.Filled.Home
        override val name: String = "Inicio"
    }
    object CropGuardian : NavigationScreen {
        override val icon = Icons.Filled.CameraAlt
        override val name = "Diagnóstico de Cultivos"
    }

    object CropsPlanning : NavigationScreen {
        override val icon = Icons.Filled.Spa
        override val name = "Cultivos y Planificación"
    }

    object PestsAndDiseases : NavigationScreen {
        override val icon = Icons.Filled.BugReport
        override val name = "Plagas y Enfermedades"
    }

    object WeatherAndWater : NavigationScreen {
        override val icon = Icons.Filled.Cloud
        override val name = "Clima y Agua"
    }

    object EconomyAndInventory : NavigationScreen {
        override val icon = Icons.Filled.AttachMoney
        override val name = "Economía y Recursos"
    }

    object Community : NavigationScreen {
        override val icon = Icons.Filled.People
        override val name = "Comunidad"
    }

    object DataAndAnalytics : NavigationScreen {
        override val icon = Icons.AutoMirrored.Filled.ShowChart
        override val name = "Datos y Comparadores"
    }

    object EducationAndMaintenance : NavigationScreen {
        override val icon = Icons.Filled.School
        override val name = "Educación y Mantenimiento"
    }

    object Settings : NavigationScreen {
        override val icon = Icons.Filled.Settings
        override val name = "Ajustes y Privacidad"
    }

    object GuideAndAbout : NavigationScreen {
        override val icon = Icons.Filled.Info
        override val name = "Guía y Acerca de"
    }
}