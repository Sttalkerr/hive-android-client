package com.hivestudio.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.components.SettingSwitchCard
import com.hivestudio.ui.model.AuthFormUi
import com.hivestudio.ui.model.LoadState

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
) {
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var stageName by rememberSaveable { mutableStateOf("") }
    val profileState = viewModel.profileState.collectAsStateWithLifecycle().value
    val message = viewModel.messageState.collectAsStateWithLifecycle().value
    val hasSession = viewModel.hasSessionState.collectAsStateWithLifecycle().value
    val form = AuthFormUi(
        email = email,
        password = password,
        stageName = stageName,
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Настройки",
                subtitle = "Профиль, вход продюсера и локальные параметры интерфейса.",
            )
        }

        item {
            SettingSwitchCard(
                title = "Уведомления о статистике",
                description = "Локальная настройка интерфейса продюсера.",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Профиль продюсера",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    when (val current = profileState) {
                        LoadState.Loading -> CircularProgressIndicator()
                        is LoadState.Error -> Text(current.message, color = MaterialTheme.colorScheme.error)
                        is LoadState.Success -> {
                            Text("Email: ${current.data.email}")
                            Text("Сценическое имя: ${current.data.stageName}")
                            Text("Создан: ${current.data.createdAt}")
                        }
                    }
                    Button(
                        onClick = { viewModel.refreshProfile() },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Обновить профиль")
                    }
                    Button(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hasSession,
                    ) {
                        Text("Выйти")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Вход и регистрация",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Тестовый вход: producer@hivestudio.dev / secret123",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Пароль") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = stageName,
                        onValueChange = { stageName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Сценическое имя") },
                        singleLine = true,
                    )
                    if (message.isNotBlank()) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Button(
                        onClick = { viewModel.login(form.email, form.password) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = form.canLogin,
                    ) {
                        Text("Войти")
                    }
                    Button(
                        onClick = { viewModel.register(form.email, form.password, form.stageName) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = form.canRegister,
                    ) {
                        Text("Зарегистрироваться")
                    }
                }
            }
        }
    }
}
