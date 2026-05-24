package com.hivestudio.ui.screens.auth

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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun AuthScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    var mode by rememberSaveable { mutableStateOf(AuthMode.Login) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var stageName by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val canLogin = email.isNotBlank() && password.isNotBlank()
    val canRegister = canLogin &&
        stageName.isNotBlank() &&
        repeatPassword.isNotBlank() &&
        repeatPassword == password

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title = "Профиль продюсера",
                subtitle = "Отдельный экран входа и регистрации для работы с личной аналитикой, своими битами и загрузкой релизов.",
                centered = true,
            )
        }

        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                AuthMode.entries.forEachIndexed { index, authMode ->
                    SegmentedButton(
                        selected = mode == authMode,
                        onClick = { mode = authMode },
                        shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = AuthMode.entries.size,
                        ),
                    ) {
                        Text(authMode.title)
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
                        text = if (mode == AuthMode.Login) "Вход" else "Регистрация",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Тестовый доступ: producer@hivestudio.dev / secret123",
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

                    if (mode == AuthMode.Register) {
                        OutlinedTextField(
                            value = stageName,
                            onValueChange = { stageName = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Сценическое имя") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = repeatPassword,
                            onValueChange = { repeatPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Повтор пароля") },
                            singleLine = true,
                            supportingText = {
                                if (repeatPassword.isNotBlank() && repeatPassword != password) {
                                    Text("Пароли должны совпадать")
                                }
                            },
                        )
                    }

                    if (message.isNotBlank()) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    if (isLoading) {
                        CircularProgressIndicator()
                    }

                    Button(
                        onClick = {
                            if (mode == AuthMode.Login) {
                                viewModel.login(email, password, onSuccess)
                            } else {
                                viewModel.register(email, password, stageName, onSuccess)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = if (mode == AuthMode.Login) canLogin else canRegister,
                    ) {
                        Text(if (mode == AuthMode.Login) "Войти" else "Создать профиль")
                    }

                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Назад")
                    }
                }
            }
        }
    }
}

private enum class AuthMode(
    val title: String,
) {
    Login("Вход"),
    Register("Регистрация"),
}
