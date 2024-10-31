package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.search
import budgetmanager.composeapp.generated.resources.select_icon
import budgetmanager.composeapp.generated.resources.select_icon_title
import org.example.project.domain.models.IconData
import org.jetbrains.compose.resources.stringResource

@Composable
fun IconPicker(
    searchText: String,
    onSearchTextUpdated: (String) -> Unit,
    isLoading: Boolean,
    icons: List<IconData>,
    onIconClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val state = rememberLazyGridState(0)
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .width(500.dp)
                .height(600.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.select_icon_title),
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextUpdated,
                label = { Text(stringResource(Res.string.search)) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.width(300.dp)
            )
            if (isLoading)
                CircularProgressIndicator(modifier = Modifier.padding(50.dp))
            else
                LazyVerticalGrid(
                    state = state,
                    columns = GridCells.Adaptive(80.dp),
                ) {
                    items(icons.size) { index ->
                        val icon = icons[index]
                        icon.image?.let {
                            IconItem(
                                name = icon.name,
                                imageVector = it,
                                onClick = { onIconClick(icon.id) }
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun IconItem(
    name:String,
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(10.dp)

    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = name,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}