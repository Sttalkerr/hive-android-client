package com.hivestudio.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.components.SettingSwitchCard

@Composable
fun SettingsScreen() {
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Настройки",
                subtitle = "Локальные параметры интерфейса и будущие настройки подключения к API.",
            )
        }

        item {
            SettingSwitchCard(
                title = "Уведомления о статистике",
                description = "Этот экран пока локальный. Позже сюда добавим профиль продюсера и настройки API.",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
            )
        }
    }
}
