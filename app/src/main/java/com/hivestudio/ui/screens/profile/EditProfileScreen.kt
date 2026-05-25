package com.hivestudio.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.ProfileAvatar
import com.hivestudio.ui.model.LoadState

@Composable
fun EditProfileScreen(
    onOpenAddBeat: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    var localAvatarUri by rememberSaveable { mutableStateOf<String?>(null) }

    var stageName by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var contactTag by rememberSaveable { mutableStateOf("") }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            localAvatarUri = uri.toString()
            viewModel.uploadAvatar(context, uri)
        }
    }

    LaunchedEffect(profileState) {
        val profile = (profileState as? LoadState.Success)?.data ?: return@LaunchedEffect
        stageName = profile.stageName
        bio = profile.bio
        city = profile.city
        contactTag = profile.contactTag
    }

    LaunchedEffect(Unit) {
        viewModel.refreshProfile()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Редактирование профиля",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        when (val current = profileState) {
            LoadState.Loading -> item { CircularProgressIndicator() }
            is LoadState.Error -> item { Text(current.message) }
            is LoadState.Success -> {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            ProfileAvatar(
                                stageName = current.data.stageName,
                                avatarUrl = current.data.avatarUrl,
                                localAvatarUri = localAvatarUri,
                                modifier = Modifier
                                    .fillMaxWidth(0.36f)
                                    .align(Alignment.CenterHorizontally)
                                    .aspectRatio(1f),
                            )

                            OutlinedButton(
                                onClick = { avatarPicker.launch("image/*") },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Сменить аватар")
                            }

                            Button(
                                onClick = onOpenAddBeat,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Добавить бит")
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
                            OutlinedTextField(
                                value = stageName,
                                onValueChange = { stageName = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Никнейм") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Город") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = contactTag,
                                onValueChange = { contactTag = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Контакт / тег") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Короткое описание") },
                                minLines = 4,
                            )

                            if (message.isNotBlank()) {
                                Text(
                                    text = message,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            Button(
                                onClick = { viewModel.saveProfile(stageName, bio, city, contactTag) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Сохранить профиль")
                            }

                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Назад")
                            }

                            OutlinedButton(
                                onClick = viewModel::logout,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Выйти")
                            }
                        }
                    }
                }
            }
        }
    }
}
