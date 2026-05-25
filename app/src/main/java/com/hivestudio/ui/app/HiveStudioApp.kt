package com.hivestudio.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
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
import com.hivestudio.ui.screens.auth.AuthScreen
import com.hivestudio.ui.screens.beatdetails.BeatDetailsScreen
import com.hivestudio.ui.screens.beats.BeatsScreen
import com.hivestudio.ui.screens.catalog.CatalogScreen
import com.hivestudio.ui.screens.dashboard.DashboardScreen
import com.hivestudio.ui.screens.editbeat.EditBeatScreen
import com.hivestudio.ui.screens.metricdetails.MetricDetailsScreen
import com.hivestudio.ui.model.AnalyticsMetricType
import com.hivestudio.ui.screens.profile.ProfileScreen

@Composable
fun HiveStudioApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomRoutes = BottomDestination.entries.map(BottomDestination::route).toSet()
    val showBottomBar = currentDestination?.route in bottomRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!showBottomBar) return@Scaffold
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
                                    BottomDestination.Catalog -> Icons.Outlined.Search
                                    BottomDestination.Dashboard -> Icons.Outlined.BarChart
                                    BottomDestination.Beats -> Icons.Outlined.LibraryMusic
                                    BottomDestination.Profile -> Icons.Outlined.Person
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
            startDestination = BottomDestination.Catalog.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomDestination.Catalog.route) {
                CatalogScreen(
                    onOpenBeat = { beatId ->
                        navController.navigate("beat/$beatId")
                    },
                )
            }
            composable(BottomDestination.Dashboard.route) {
                DashboardScreen(
                    onOpenBeat = { beatId ->
                        navController.navigate("beat/$beatId")
                    },
                    onOpenMetric = { metricType ->
                        navController.navigate("metric/$metricType")
                    }
                )
            }
            composable(BottomDestination.Beats.route) {
                BeatsScreen(
                    onOpenBeat = { beatId ->
                        navController.navigate("beat/$beatId")
                    }
                )
            }
            composable(BottomDestination.Profile.route) {
                ProfileScreen(
                    onOpenAuth = { navController.navigate("auth") },
                    onOpenAddBeat = { navController.navigate("add_beat") },
                )
            }
            composable("auth") {
                AuthScreen(
                    onSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
            composable("add_beat") {
                AddBeatScreen()
            }
            composable(
                route = "beat/{beatId}",
                arguments = listOf(navArgument("beatId") { type = NavType.StringType }),
            ) { backStackEntry ->
                BeatDetailsScreen(
                    beatId = backStackEntry.arguments?.getString("beatId").orEmpty(),
                    onEdit = { editableBeatId ->
                        navController.navigate("edit_beat/$editableBeatId")
                    },
                    onDeleted = { navController.popBackStack() },
                )
            }
            composable(
                route = "edit_beat/{beatId}",
                arguments = listOf(navArgument("beatId") { type = NavType.StringType }),
            ) { backStackEntry ->
                EditBeatScreen(
                    beatId = backStackEntry.arguments?.getString("beatId").orEmpty(),
                    onSaved = { navController.popBackStack() },
                )
            }
            composable(
                route = "metric/{metricType}",
                arguments = listOf(navArgument("metricType") { type = NavType.StringType }),
            ) { backStackEntry ->
                MetricDetailsScreen(
                    metricType = AnalyticsMetricType.fromRouteKey(
                        backStackEntry.arguments?.getString("metricType").orEmpty()
                    )
                )
            }
        }
    }
}
