package com.camerax.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.camerax.R
import com.camerax.presentation.ui.about.AboutScreen
import com.camerax.presentation.ui.camera.CameraScreen
import com.camerax.presentation.ui.camera.CameraViewModel
import com.camerax.presentation.ui.gallery.GalleryScreen
import com.camerax.presentation.ui.gallery.GalleryViewModel
import com.camerax.presentation.ui.gallery.MediaViewerScreen
import com.camerax.presentation.ui.scanner.ScanHistoryScreen
import com.camerax.presentation.ui.scanner.ScanHistoryViewModel
import com.camerax.presentation.ui.settings.SettingsScreen
import com.camerax.presentation.ui.settings.SettingsViewModel

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int,
)

@Composable
fun AppNavigation(
    cameraViewModel: CameraViewModel,
    galleryViewModel: GalleryViewModel,
    scanHistoryViewModel: ScanHistoryViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberNavController()

    val bottomNavItems =
        listOf(
            BottomNavItem(Screen.Camera.route, Icons.Default.CameraAlt, R.string.nav_camera),
            BottomNavItem(Screen.Gallery.route, Icons.Default.PhotoLibrary, R.string.nav_gallery),
            BottomNavItem(Screen.ScanHistory.route, Icons.Default.History, R.string.nav_history),
            BottomNavItem(Screen.Settings.route, Icons.Default.Settings, R.string.nav_settings),
        )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val hideBottomBar =
        currentDestination?.route?.startsWith("media_viewer") == true ||
            currentDestination?.route == Screen.About.route

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 0.dp,
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.labelResId),
                                )
                            },
                            label = { Text(stringResource(item.labelResId)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors =
                                NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Camera.route,
            modifier = if (hideBottomBar) Modifier else Modifier.padding(innerPadding),
        ) {
            composable(Screen.Camera.route) {
                CameraScreen(
                    viewModel = cameraViewModel,
                    onNavigateToGallery = {
                        navController.navigate(Screen.Gallery.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(Screen.Gallery.route) {
                GalleryScreen(
                    viewModel = galleryViewModel,
                    onMediaClick = { mediaId ->
                        navController.navigate(Screen.MediaViewer.createRoute(mediaId))
                    },
                )
            }

            composable(
                route = Screen.MediaViewer.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
                MediaViewerScreen(
                    mediaId = mediaId,
                    viewModel = galleryViewModel,
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Screen.ScanHistory.route) {
                ScanHistoryScreen(viewModel = scanHistoryViewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToAbout = {
                        navController.navigate(Screen.About.route)
                    },
                )
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
