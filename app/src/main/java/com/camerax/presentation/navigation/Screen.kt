package com.camerax.presentation.navigation

sealed class Screen(val route: String) {
    data object Camera : Screen("camera")

    data object Gallery : Screen("gallery")

    data object MediaViewer : Screen("media_viewer/{mediaId}") {
        fun createRoute(mediaId: Long): String = "media_viewer/$mediaId"
    }

    data object ScanHistory : Screen("scan_history")

    data object Settings : Screen("settings")

    data object About : Screen("about")
}
