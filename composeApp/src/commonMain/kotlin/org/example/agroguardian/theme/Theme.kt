// Generated using MaterialKolor Builder version 1.2.1 (103)
// https://materialkolor.com/?color_seed=FF388E3C&color_primary=FF388E3C&color_secondary=FF009688&color_tertiary=FF8D6E63&color_neutral=FF747871&dark_mode=false&package_name=org.example.agroguardian

package org.example.agroguardian.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState

@Composable
fun AgroGuardianTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = isDarkTheme,
        style = PaletteStyle.TonalSpot,
        primary = Primary,
        secondary = Secondary,
        tertiary = Tertiary,
        neutral = Neutral,
    )
    
    DynamicMaterialTheme(
        state = dynamicThemeState,
        animate = true,
        content = content,
    )
}