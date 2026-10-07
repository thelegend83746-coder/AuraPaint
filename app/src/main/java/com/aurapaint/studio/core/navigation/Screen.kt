package com.aurapaint.studio.core.navigation

sealed class Screen {
    object Home : Screen()
    data class Editor(val projectId: String) : Screen()
    object Settings : Screen()
}
