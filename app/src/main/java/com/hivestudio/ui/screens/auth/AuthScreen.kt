package com.hivestudio.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    onOpenRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    AuthScreenScaffold {
        AuthCard(
            title = "Вход",
            footerText = "Нет аккаунта?",
            footerActionLabel = "Регистрация",
            onFooterAction = onOpenRegister,
        ) {
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
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )

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
                onClick = { viewModel.login(email, password, onSuccess) },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && password.isNotBlank(),
            ) {
                Text("Войти")
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onSuccess: () -> Unit,
    onOpenLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var stageName by rememberSaveable { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    AuthScreenScaffold {
        AuthCard(
            title = "Регистрация",
            footerText = "Уже есть аккаунт?",
            footerActionLabel = "Вход",
            onFooterAction = onOpenLogin,
        ) {
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
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )
            OutlinedTextField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Повтор пароля") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                supportingText = {
                    if (repeatPassword.isNotBlank() && repeatPassword != password) {
                        Text("Пароли должны совпадать")
                    }
                },
            )
            OutlinedTextField(
                value = stageName,
                onValueChange = { stageName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Никнейм") },
                singleLine = true,
            )

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
                onClick = { viewModel.register(email, password, stageName, onSuccess) },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() &&
                    password.isNotBlank() &&
                    repeatPassword.isNotBlank() &&
                    repeatPassword == password &&
                    stageName.isNotBlank(),
            ) {
                Text("Создать профиль")
            }
        }
    }
}

@Composable
private fun AuthScreenScaffold(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = "Hive Studio",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            content()
        }
    }
}

@Composable
private fun AuthCard(
    title: String,
    footerText: String,
    footerActionLabel: String,
    onFooterAction: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
            content()
            TextButton(
                onClick = onFooterAction,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("$footerText $footerActionLabel")
            }
        }
    }
}
