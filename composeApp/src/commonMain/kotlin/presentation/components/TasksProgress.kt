package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun TasksProgress(
    modifier: Modifier = Modifier,
    completedTasks: Int,
    pendingTasks: Int
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val task = if (pendingTasks > 1) "tasks" else "task"
            Text(
                text = "$pendingTasks $task left",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )
            val total = if(pendingTasks + completedTasks > 0) (pendingTasks + completedTasks).toFloat() else 1f
            Box(
                modifier = Modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize(),
                    progress = {
                        completedTasks / total
                    },
                    strokeCap = StrokeCap.Butt,
                    strokeWidth = 8.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
                )
                val percent = (completedTasks / total * 100).coerceIn(0f, 100f).roundToInt()
                Text(
                    text = "$percent%",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                )
            }
        }
    }
}