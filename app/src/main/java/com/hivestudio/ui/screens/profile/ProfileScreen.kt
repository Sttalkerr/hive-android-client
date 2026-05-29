package com.hivestudio.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.ProfileAvatar
import com.hivestudio.ui.components.SettingSwitchCard
import com.hivestudio.ui.format.RubleFormatter
import com.hivestudio.ui.model.LoadState
import com.hivestudio.ui.model.ProfileUi

@Composable
fun ProfileScreen(
    onOpenAuth: () -> Unit,
    onOpenEditProfile: () -> Unit,
    darkThemeEnabled: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val hasSession by viewModel.hasSession.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Профиль",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
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
                            text = "Войди или зарегистрируйся, чтобы открыть свой профиль и работать со своими битами.",
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
                LoadState.Loading -> item { }
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
                            onOpenEditProfile = onOpenEditProfile,
                        )
                    }

                    item {
                        ProfileStatsRow(profile = current.data)
                    }

                    item {
                        SettingSwitchCard(
                            title = "Тёмная тема",
                            description = "Переключение оформления приложения.",
                            checked = darkThemeEnabled,
                            onCheckedChange = onThemeChanged,
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                            ),
                        ) {
                            Text("Выйти из аккаунта")
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
    onOpenEditProfile: () -> Unit,
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
                    if (profile.city.isNotBlank()) {
                        Text(
                            text = profile.city,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Button(
                onClick = onOpenEditProfile,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Редактировать профиль")
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
    Card(
        modifier = modifier.height(96.dp),
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
