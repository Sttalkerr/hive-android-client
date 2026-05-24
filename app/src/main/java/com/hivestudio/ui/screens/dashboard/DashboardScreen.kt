package com.hivestudio.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hivestudio.ui.components.DashboardMetricCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.model.LoadState

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Hive Studio",
                subtitle = "Дашборд продюсера с аналитикой, загруженной с сервера.",
            )
        }

        when (val current = state) {
            LoadState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> {
                item {
                    Text(current.message)
                }
            }

            is LoadState.Success -> {
                items(current.data) { metric ->
                    DashboardMetricCard(metric = metric)
                }
            }
        }
    }
}
