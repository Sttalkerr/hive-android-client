package com.hivestudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hivestudio.data.preferences.SearchHistoryStore
import com.hivestudio.data.preferences.ThemePreferenceStore
import com.hivestudio.data.session.SessionStore
import com.hivestudio.ui.app.HiveStudioApp
import com.hivestudio.ui.theme.HiveStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStore.initialize(applicationContext)
        SearchHistoryStore.initialize(applicationContext)
        ThemePreferenceStore.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            HiveStudioRoot()
        }
    }
}

@Composable
private fun HiveStudioRoot() {
    var darkThemeEnabled by remember { mutableStateOf(ThemePreferenceStore.isDarkThemeEnabled()) }
    HiveStudioTheme(
        darkTheme = darkThemeEnabled,
    ) {
        HiveStudioApp(
            darkThemeEnabled = darkThemeEnabled,
            onThemeChanged = {
                darkThemeEnabled = it
                ThemePreferenceStore.setDarkThemeEnabled(it)
            },
        )
    }
}
