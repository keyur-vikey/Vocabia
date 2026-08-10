package com.mithun.vocabia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mithun.vocabia.repository.PracticeRepository

@Composable
fun StatsScreen(categoryStats: List<PracticeRepository.CategoryStats>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Progress", style = MaterialTheme.typography.titleLarge)
        }

        val overallTotal = categoryStats.sumOf { it.total }
        val overallFinished = categoryStats.sumOf { it.finished }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(text = "$overallFinished / $overallTotal words finished", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (overallTotal > 0) overallFinished.toFloat() / overallTotal else 0f },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp))
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            categoryStats.forEach { stat ->
                CategoryStatsRow(stat)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CategoryStatsRow(stat: PracticeRepository.CategoryStats) {
    val accent = categoryColor(stat.category)

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = categoryIcon(stat.category),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = categoryLabel(stat.category),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(text = "${stat.finished}/${stat.total} finished", fontSize = 13.sp)
        }
        Spacer(Modifier.height(6.dp))
        // stacked bar: finished / learning / new
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(androidx.compose.ui.graphics.Color(0xFFE5E7EB))
        ) {
            if (stat.finished > 0) Box(Modifier.weight(stat.finished.toFloat()).fillMaxSize().background(accent))
            if (stat.learning > 0) Box(Modifier.weight(stat.learning.toFloat()).fillMaxSize().background(accent.copy(alpha = 0.4f)))
            if (stat.new > 0) Box(Modifier.weight(stat.new.toFloat()).fillMaxSize())
        }
    }
}
