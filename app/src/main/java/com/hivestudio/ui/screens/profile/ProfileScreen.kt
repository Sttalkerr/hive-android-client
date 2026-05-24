package com.hivestudio.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.ProfileAvatar
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.format.RubleFormatter
import com.hivestudio.ui.model.LoadState
import com.hivestudio.ui.model.ProfileUi

@Composable
fun ProfileScreen(
    onOpenAuth: () -> Unit,
    onOpenAddBeat: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val hasSession by viewModel.hasSession.collectAsStateWithLifecycle()
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
        localAvatarUri = null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Профиль",
                subtitle = "Редактирование сценического имени, аватара и рабочих данных продюсера.",
                centered = true,
            )
        }

        if (!hasSession) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Личный профиль еще не открыт",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "После входа ты получишь свои биты, персональную статистику, загрузку новых релизов и редактирование данных продюсера.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = onOpenAuth,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Открыть вход и регистрацию")
                        }
                    }
                }
            }
        } else {
            when (val current = profileState) {
                LoadState.Loading -> item { CircularProgressIndicator() }
                is LoadState.Error -> item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(current.message)
                            Button(
                                onClick = onOpenAuth,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Открыть вход")
                            }
                        }
                    }
                }
                is LoadState.Success -> {
                    item {
                        ProfileHeroCard(
                            profile = current.data,
                            avatarLocalUri = localAvatarUri,
                            onChangeAvatar = { avatarPicker.launch("image/*") },
                            onOpenAddBeat = onOpenAddBeat,
                        )
                    }

                    item {
                        ProfileStatsRow(profile = current.data)
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(
                                    text = "Данные продюсера",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                OutlinedTextField(
                                    value = stageName,
                                    onValueChange = { stageName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Сценическое имя") },
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
}

@Composable
private fun ProfileHeroCard(
    profile: ProfileUi,
    avatarLocalUri: String?,
    onChangeAvatar: () -> Unit,
    onOpenAddBeat: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileAvatar(
                    stageName = profile.stageName,
                    avatarUrl = profile.avatarUrl,
                    localAvatarUri = avatarLocalUri,
                    modifier = Modifier
                        .weight(0.28f)
                        .aspectRatio(1f),
                )
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.weight(0.72f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = profile.stageName,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = profile.contactTag.ifBlank { "Контакт пока не указан" },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Профиль создан: ${profile.createdAt.substringBefore("T")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onChangeAvatar,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Сменить аватар")
                }
                Button(
                    onClick = onOpenAddBeat,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Добавить бит")
                }
            }
        }
    }
}

@Composable
private fun ProfileStatsRow(
    profile: ProfileUi,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProfileStatCard(
            title = "Битов",
            value = profile.beatsCount.toString(),
            modifier = Modifier.weight(1f),
        )
        ProfileStatCard(
            title = "Прослушиваний",
            value = profile.totalPlays.toString(),
            modifier = Modifier.weight(1f),
        )
        ProfileStatCard(
            title = "Выручка",
            value = RubleFormatter.format(profile.totalRevenue),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
