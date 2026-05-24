package com.hivestudio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hivestudio.ui.model.DashboardMetricUi
import com.hivestudio.ui.theme.BlueAccent
import com.hivestudio.ui.theme.GraphiteSoft

@Composable
fun DashboardMetricCard(
    metric: DashboardMetricUi,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (emphasized) BlueAccent.copy(alpha = 0.16f) else GraphiteSoft,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (emphasized) 0.42f else 0.22f)
                    .height(4.dp)
                    .background(if (emphasized) BlueAccent else MaterialTheme.colorScheme.secondary),
            )
            Text(
                text = metric.label,
                style = MaterialTheme.typography.titleMedium,
                color = if (emphasized) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = metric.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = metric.hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
