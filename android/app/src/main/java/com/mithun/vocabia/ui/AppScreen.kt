package com.mithun.vocabia.ui

sealed class AppScreen {
    data object Home : AppScreen()
    data object Practice : AppScreen()
    data object Progress : AppScreen()
}
