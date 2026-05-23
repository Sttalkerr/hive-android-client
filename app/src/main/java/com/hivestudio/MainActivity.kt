package com.hivestudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.theme.HiveStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiveStudioTheme {
                HiveStudioApp()
            }
        }
    }
}

private enum class HomeTab(val title: String) {
    Dashboard("Статистика"),
    Beats("Мои биты"),
    Add("Добавить"),
    Settings("Настройки"),
}

@Composable
private fun HiveStudioApp() {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.Dashboard) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    HomeTab.Dashboard -> Icons.Outlined.BarChart
                                    HomeTab.Beats -> Icons.Outlined.LibraryMusic
                                    HomeTab.Add -> Icons.Outlined.AddBox
                                    HomeTab.Settings -> Icons.Outlined.Settings
                                },
                                contentDescription = tab.title,
                            )
                        },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Hive Studio",
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = when (selectedTab) {
                        HomeTab.Dashboard -> "Здесь будет dashboard продюсера."
                        HomeTab.Beats -> "Здесь будет список битов с поиском."
                        HomeTab.Add -> "Здесь будет экран добавления MP3-бита."
                        HomeTab.Settings -> "Здесь будут настройки и переключатель темы."
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
        }
    }
}
