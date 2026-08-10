package com.mithun.vocabia.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mithun.vocabia.repository.PracticeRepository

@Composable
fun ProgressScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { PracticeRepository(context) }

    var loading by remember { mutableStateOf(true) }
    var categoryStats by remember { mutableStateOf<List<PracticeRepository.CategoryStats>>(emptyList()) }

    LaunchedEffect(Unit) {
        repository.ensureSeeded()
        categoryStats = repository.statsByCategory()
        loading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            StatsScreen(categoryStats = categoryStats, onBack = onBack)
        }
    }
}
