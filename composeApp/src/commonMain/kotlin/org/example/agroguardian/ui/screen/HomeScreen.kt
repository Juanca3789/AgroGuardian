package org.example.agroguardian.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.agroguardian.theme.AgroGuardianTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen() {
    HomeInterface()
}

@Composable
fun HomeInterface() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}

@Preview
@Composable
fun HomeInterfacePreview() {
    AgroGuardianTheme {
        HomeInterface()
    }
}