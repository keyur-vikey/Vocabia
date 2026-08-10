package com.mithun.vocabia.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mithun.vocabia.data.WordEntity
import com.mithun.vocabia.repository.PracticeCard
import com.mithun.vocabia.repository.SwipeDirection
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val SWIPE_THRESHOLD_PX = 300f

@Composable
fun CardStack(
    cards: List<PracticeCard>,
    currentIndex: Int,
    onSwiped: (WordEntity, SwipeDirection) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // deck-on-floor: peeking cards behind the current one
        for (depth in 2 downTo 1) {
            val peekIndex = currentIndex + depth
            if (peekIndex < cards.size) {
                val word = cards[peekIndex].word
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .fillMaxHeight(0.72f)
                        .graphicsLayer {
                            translationX = depth * 10f
                            translationY = depth * 14f
                            rotationZ = depth * 3f
                        }
                        .alpha(0.5f + (0.15f * (3 - depth))),
                    colors = CardDefaults.cardColors(containerColor = categoryColor(word.category).copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(20.dp)
                ) {}
            }
        }

        if (currentIndex < cards.size) {
            SwipeableTopCard(card = cards[currentIndex], onSwiped = onSwiped)
        }
    }
}

@Composable
private fun SwipeableTopCard(
    card: PracticeCard,
    onSwiped: (WordEntity, SwipeDirection) -> Unit
) {
    val word = card.word
    var revealCount by remember(word.id) { mutableIntStateOf(0) }
    val offsetX = remember(word.id) { Animatable(0f) }
    val offsetY = remember(word.id) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val maxReveal = remember(word.id) { maxRevealCount(word) }

    val dragTint = when {
        offsetX.value > 60f -> Color(0xFF22C55E).copy(alpha = 0.25f)
        offsetX.value < -60f -> Color(0xFFEF4444).copy(alpha = 0.25f)
        offsetY.value > 60f -> Color(0xFFFACC15).copy(alpha = 0.25f)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .fillMaxHeight(0.72f)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = offsetX.value / 25f
            }
            .clickable {
                if (revealCount < maxReveal) revealCount += 1
            }
            .pointerInput(word.id) {
                detectDragGestures(
                    onDragEnd = {
                        val x = offsetX.value
                        val y = offsetY.value
                        scope.launch {
                            when {
                                y > SWIPE_THRESHOLD_PX && y > abs(x) -> {
                                    offsetY.animateTo(1200f, tween(250))
                                    onSwiped(word, SwipeDirection.DOWN)
                                }
                                x > SWIPE_THRESHOLD_PX -> {
                                    offsetX.animateTo(1200f, tween(250))
                                    onSwiped(word, SwipeDirection.RIGHT)
                                }
                                x < -SWIPE_THRESHOLD_PX -> {
                                    offsetX.animateTo(-1200f, tween(250))
                                    onSwiped(word, SwipeDirection.LEFT)
                                }
                                else -> {
                                    offsetX.animateTo(0f, tween(200))
                                    offsetY.animateTo(0f, tween(200))
                                }
                            }
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                        offsetY.snapTo(offsetY.value + dragAmount.y)
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(dragTint)) {
            WordCardContent(word = word, revealCount = revealCount)
        }
    }
}
