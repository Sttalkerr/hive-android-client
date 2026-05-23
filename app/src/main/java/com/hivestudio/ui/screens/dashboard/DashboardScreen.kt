package com.hivestudio.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.components.DashboardMetricCard
import com.hivestudio.ui.components.ScreenHeader
import com.hivestudio.ui.preview.HiveStudioPreviewData

@Composable
fun DashboardScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = "Hive Studio",
                subtitle = "Дашборд продюсера с ключевой учебной аналитикой по битам.",
            )
        }

        items(HiveStudioPreviewData.dashboardMetrics) { metric ->
            DashboardMetricCard(metric = metric)
        }
    }
}
