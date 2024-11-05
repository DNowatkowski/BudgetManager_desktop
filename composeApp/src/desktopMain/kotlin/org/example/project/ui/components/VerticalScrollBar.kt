package org.example.project.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun VerticalScrollBar(
    scrollState: ScrollState,
    modifier: Modifier,
) {

    var scrollBarHeight by remember { mutableStateOf(0) }
    var innerBarHeight by remember { mutableStateOf(0) }
    val padding: Int by remember(scrollState.value, scrollBarHeight) {
        val p =
            ((scrollState.value.toDouble() / scrollState.maxValue.toDouble()) * (scrollBarHeight - innerBarHeight)).toInt()

        mutableStateOf(p)
    }
    Box(
        modifier = modifier.fillMaxHeight().background(color = Color.Transparent)
            .width(8.dp)
            .onGloballyPositioned {
                scrollBarHeight = it.size.height
            }.padding(top = padding.dp)
    ) {
        Box(
            modifier = Modifier.clip(MaterialTheme.shapes.small).background(color = Color.LightGray)
                .height((scrollBarHeight / 6).dp)
                .width(8.dp).zIndex(10f)
                .onGloballyPositioned { innerBarHeight = it.size.height }
        )
    }
}


@Composable
fun VerticalScrollBar(
    listState: LazyListState,
    modifier: Modifier,
) {
    var scrollBarHeight by remember { mutableStateOf(0) }
    var innerBarHeight by remember { mutableStateOf(0) }
    val padding: Int by remember(
        listState.firstVisibleItemIndex,
        listState.firstVisibleItemScrollOffset,
        scrollBarHeight
    ) {
        val totalItems = listState.layoutInfo.totalItemsCount
        val visibleItems = listState.layoutInfo.visibleItemsInfo.size
        val totalScrollRange = totalItems - visibleItems
        val scrollFraction = if (totalScrollRange > 0) {
            ((listState.firstVisibleItemIndex + listState.firstVisibleItemScrollOffset / listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size?.toFloat()!!)) / totalScrollRange
        } else {
            0f
        }
        val p = (scrollFraction * (scrollBarHeight - innerBarHeight)).toInt()
        mutableStateOf(p)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(color = Color.Transparent)
            .width(8.dp)
            .onGloballyPositioned {
                scrollBarHeight = it.size.height
            }
            .padding(top = padding.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(color = Color.LightGray)
                .height((scrollBarHeight / 6).dp)
                .width(8.dp)
                .zIndex(10f)
                .onGloballyPositioned { innerBarHeight = it.size.height }
        )
    }
}