package com.example.shilpakala.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shilpakala.ui.auth.AuthScreen
import com.example.shilpakala.ui.auth.AuthViewModel
import com.example.shilpakala.ui.camera.CameraScreen
import com.example.shilpakala.ui.gallery.GalleryScreen
import com.example.shilpakala.ui.home.HomeScreen
import com.example.shilpakala.ui.profile.ProfileScreen
import com.example.shilpakala.ui.settings.SettingsScreen
import com.example.shilpakala.ui.settings.SettingsViewModel

private sealed class Destination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : Destination("home", "Home", Icons.Default.Home)
    data object Camera : Destination("camera", "Camera", Icons.Default.CameraAlt)
    data object Gallery : Destination("gallery", "Gallery", Icons.Default.Image)
    data object Profile : Destination("profile", "Profile", Icons.Default.Person)
    data object Settings : Destination("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun ShilpaKalaApp(authViewModel: AuthViewModel = hiltViewModel()) {
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    if (authState.user == null) {
        AuthScreen(viewModel = authViewModel)
        return
    }

    val navController = rememberNavController()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val items = listOf(Destination.Home, Destination.Camera, Destination.Gallery, Destination.Profile)
    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    onOpenCamera = { navController.navigate(Destination.Camera.route) },
                    onOpenGallery = { navController.navigate(Destination.Gallery.route) },
                    onOpenProfile = { navController.navigate(Destination.Profile.route) },
                    onOpenSettings = { navController.navigate(Destination.Settings.route) },
                    onToggleReminder = { settingsViewModel.setReminder(!settingsViewModel.state.value.reminderEnabled) }
                )
            }
            composable(Destination.Camera.route) { CameraScreen() }
            composable(Destination.Gallery.route) { GalleryScreen() }
            composable(Destination.Profile.route) { ProfileScreen(onLogout = authViewModel::logout) }
            composable(Destination.Settings.route) { SettingsScreen() }
        }
    }
}
