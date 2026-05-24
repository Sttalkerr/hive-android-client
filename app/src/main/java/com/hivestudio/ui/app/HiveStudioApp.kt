package com.hivestudio.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hivestudio.ui.navigation.BottomDestination
import com.hivestudio.ui.screens.add.AddBeatScreen
import com.hivestudio.ui.screens.beatdetails.BeatDetailsScreen
import com.hivestudio.ui.screens.beats.BeatsScreen
import com.hivestudio.ui.screens.dashboard.DashboardScreen
import com.hivestudio.ui.screens.settings.SettingsScreen

@Composable
fun HiveStudioApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                BottomDestination.entries.forEach { destination ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == destination.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    BottomDestination.Dashboard -> Icons.Outlined.BarChart
                                    BottomDestination.Beats -> Icons.Outlined.LibraryMusic
                                    BottomDestination.AddBeat -> Icons.Outlined.AddBox
                                    BottomDestination.Settings -> Icons.Outlined.Settings
                                },
                                contentDescription = destination.title,
                            )
                        },
                        label = { Text(destination.title) },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomDestination.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomDestination.Dashboard.route) {
                DashboardScreen()
            }
            composable(BottomDestination.Beats.route) {
                BeatsScreen(
                    onOpenBeat = { beatId ->
                        navController.navigate("beat/$beatId")
                    }
                )
            }
            composable(BottomDestination.AddBeat.route) {
                AddBeatScreen()
            }
            composable(BottomDestination.Settings.route) {
                SettingsScreen()
            }
            composable(
                route = "beat/{beatId}",
                arguments = listOf(navArgument("beatId") { type = NavType.StringType }),
            ) { backStackEntry ->
                BeatDetailsScreen(
                    beatId = backStackEntry.arguments?.getString("beatId").orEmpty(),
                )
            }
        }
    }
}
