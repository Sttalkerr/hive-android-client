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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hivestudio.data.session.SessionStore
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
import com.hivestudio.ui.screens.profile.EditProfileScreen
import com.hivestudio.ui.screens.profile.ProfileScreen
import com.hivestudio.ui.theme.HiveStudioTheme

@Composable
fun HiveStudioApp(
    darkThemeEnabled: Boolean,
    onThemeChanged: (Boolean) -> Unit,
) {
    var hasSession by remember { mutableStateOf(SessionStore.hasActiveSession()) }

    if (!hasSession) {
        HiveStudioTheme(darkTheme = true) {
            AuthScreen(
                onSuccess = { hasSession = true },
                onBack = {},
            )
        }
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomDestination.entries.forEach { destination ->
                    val currentRoute = currentDestination?.route
                    val selected = when (destination) {
                        BottomDestination.Catalog -> currentRoute == BottomDestination.Catalog.route
                        BottomDestination.Dashboard -> currentRoute == BottomDestination.Dashboard.route ||
                            currentRoute?.startsWith("metric/") == true
                        BottomDestination.Beats -> currentRoute == BottomDestination.Beats.route ||
                            currentRoute == "add_beat" ||
                            currentRoute?.startsWith("edit_beat/") == true
                        BottomDestination.Profile -> currentRoute == BottomDestination.Profile.route ||
                            currentRoute == "edit_profile"
                    } || currentDestination
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
                    onOpenAddBeat = {
                        navController.navigate("add_beat")
                    },
                    onOpenBeat = { beatId ->
                        navController.navigate("beat/$beatId")
                    }
                )
            }
            composable(BottomDestination.Profile.route) {
                ProfileScreen(
                    onOpenAuth = { hasSession = false },
                    onOpenEditProfile = { navController.navigate("edit_profile") },
                    darkThemeEnabled = darkThemeEnabled,
                    onThemeChanged = onThemeChanged,
                    onLogout = {
                        hasSession = false
                    },
                )
            }
            composable("edit_profile") {
                EditProfileScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable("add_beat") {
                AddBeatScreen(
                    onUploaded = { beatId ->
                        navController.navigate("beat/$beatId") {
                            launchSingleTop = true
                        }
                    }
                )
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
