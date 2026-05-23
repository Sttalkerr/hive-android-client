package com.hivestudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.hivestudio.ui.app.HiveStudioApp
import com.hivestudio.ui.theme.HiveStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiveStudioTheme {
                HiveStudioRoot()
            }
        }
    }
}

@Composable
private fun HiveStudioRoot() {
    HiveStudioApp()
}
