package com.mithun.vocabia.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mithun.vocabia.repository.PracticeCard
import com.mithun.vocabia.repository.PracticeRepository
import com.mithun.vocabia.repository.SwipeDirection
import kotlinx.coroutines.launch

@Composable
fun DeckScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { PracticeRepository(context) }
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    var cards by remember { mutableStateOf<List<PracticeCard>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var finishedCount by remember { mutableIntStateOf(0) }
    var totalCount by remember { mutableIntStateOf(0) }

    suspend fun loadNewSession() {
        repository.advanceSession()
        cards = repository.buildSession()
        currentIndex = 0
        val (finished, total) = repository.stats()
        finishedCount = finished
        totalCount = total
    }

    LaunchedEffect(Unit) {
        repository.ensureSeeded()
        loadNewSession()
        loading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Finished: $finishedCount / $totalCount",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (currentIndex >= cards.size) {
                    Text(
                        text = "Session complete!",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    LaunchedEffect(currentIndex) {
                        loadNewSession()
                    }
                } else {
                    CardStack(
                        cards = cards,
                        currentIndex = currentIndex,
                        onSwiped = { word, direction ->
                            scope.launch {
                                repository.recordSwipe(word, direction)
                                currentIndex += 1
                            }
                        }
                    )
                }
            }
        }
    }
}
