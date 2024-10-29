package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import org.example.project.domain.models.IconData

@Composable
fun IconPicker(
    icons: List<IconData>,
    onIconClick: (IconData) -> Unit
) {
    val state = rememberLazyGridState(0)
    Popup {
        Column(modifier = Modifier.width(300.dp).height(400.dp)) {
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Search") },
                modifier = Modifier.width(200.dp)
            )
            LazyVerticalGrid(
                state = state,
                columns = GridCells.Fixed(8),
                modifier = Modifier.width(300.dp)
            ) {
                items(icons.size){
                    IconItem(icons[it], onClick = { onIconClick(icons[it]) })
                }
            }
        }

    }
}

@Composable
private fun IconItem(icon: IconData, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        icon.image?.let { Icon(imageVector = it, contentDescription = icon.name) }
        Text(icon.name, style = MaterialTheme.typography.labelMedium)
    }

}