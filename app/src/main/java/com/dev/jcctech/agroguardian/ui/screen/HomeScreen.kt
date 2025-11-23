package com.dev.jcctech.agroguardian.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.jcctech.agroguardian.ui.theme.AgroGuardianTheme

@Composable
fun HomeScreen() {
    HomeInterface()
}

@Composable
fun HomeInterface() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

    }
}

@Preview
@Composable
fun HomeInterfacePreview() {
    AgroGuardianTheme {
        HomeInterface()
    }
}