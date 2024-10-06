package org.example.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.KeywordData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryItem(
    title: String,
    keywords: List<KeywordData>,
    onAddKeyword: (String) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    onRemoveKeyword: (KeywordData) -> Unit,
) {
    Card(elevation = CardDefaults.cardElevation(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("$title:")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                keywords.forEach {
                    KeywordChip(
                        keyword = it,
                        onRemoveKeyword = onRemoveKeyword,
                        onKeywordUpdated = onKeywordUpdated,
                    )
                }
                AddKeywordChip(
                    onAddKeyword = onAddKeyword,
                )
            }
        }
    }
}