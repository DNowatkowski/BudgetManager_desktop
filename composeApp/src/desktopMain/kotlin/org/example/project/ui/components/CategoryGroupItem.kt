package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.CategoryData
import org.example.project.domain.models.KeywordData

@Composable
fun CategoryGroupItem(
    title: String,
    categories: List<CategoryData>,
    onAddGroup: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    onAddKeyword: (String, String) -> Unit,
    onRemoveKeyword: (KeywordData) -> Unit,
) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(style = MaterialTheme.typography.headlineSmall, text = title)
            HorizontalDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                categories.forEach {
                    CategoryItem(
                        title = it.name,
                        keywords = it.keywords,
                        onAddKeyword = { keyword -> onAddKeyword(keyword, it.id) },
                        onKeywordUpdated = onKeywordUpdated,
                        onRemoveKeyword = onRemoveKeyword,
                    )
                }
                Card(elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                        }
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.AddCircle, null, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
