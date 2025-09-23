package com.example.authapp.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush

/**
 * AutoSlidingBanner:
 * - slides ALWAYS right -> left
 * - smooth transition speed controlled with animationDurationMs
 * - stays on each slide controlled with slideDurationMs
 * - colors list should be at least as long as messages (or will be reused)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingBanner(
    messages: List<String>,
    colors: List<Brush>,
    modifier: Modifier = Modifier,
    slideDurationMs: Long = 4000L,    // how long each slide stays visible
    animationDurationMs: Int = 1200   // how long the slide animation takes (slower => bigger)
) {
    if (messages.isEmpty()) return

    // Duplicate the content so we can scroll seamlessly (no visible reverse jump)
    val pages = remember(messages, colors) {
        // pair each message with a color (re-use colors if needed)
        val paired = messages.mapIndexed { i, msg ->
            msg to colors[i % colors.size]
        }
        paired + paired
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    // auto-scroll loop
    LaunchedEffect(messages, colors) {
        // small delay before starting so UI stabilizes
        delay(300L)
        var current = pagerState.currentPage
        while (true) {
            delay(slideDurationMs)
            val next = current + 1
            scope.launch {
                // Animate to the next page with custom tween (controls slide speed)
                pagerState.animateScrollToPage(
                    page = next,
                    animationSpec = tween(durationMillis = animationDurationMs)
                )

                // If we've moved into the duplicated half, jump back to the first half silently.
                // Example: messages.size == N, pages.size == 2N
                // When next >= N (we're in the second block), jump to next - N
                if (next >= messages.size) {
                    val jumpTarget = next - messages.size
                    // immediate jump (no animation) to make the loop seamless
                    pagerState.scrollToPage(jumpTarget)
                    current = jumpTarget
                } else {
                    current = next
                }
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) { page ->
        val (text, bg) = pages[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bg, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2
                )
            }
        }
    }
}
