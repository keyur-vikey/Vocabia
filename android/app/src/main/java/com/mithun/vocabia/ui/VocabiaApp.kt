package com.mithun.vocabia.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun VocabiaApp() {
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    when (screen) {
        AppScreen.Home -> HomeScreen(onNavigate = { screen = it })
        AppScreen.Practice -> DeckScreen(onBack = { screen = AppScreen.Home })
        AppScreen.Progress -> ProgressScreen(onBack = { screen = AppScreen.Home })
    }
}
