package org.example.project.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import org.example.project.CsvReader.CsvReader


class ImportScreen : Screen {
    @Composable
    override fun Content() {
        var rows by remember { mutableStateOf(emptyList<String>()) }

        val launcher = rememberFilePickerLauncher(mode = PickerMode.Single) { file ->
            if (file != null) {
                rows = CsvReader.readFile(file).getOrNull() ?: listOf("error")
            }
        }
        Button(onClick = {
            launcher.launch()
        }) {
            Text("Import file")
        }
        LazyColumn {
            items(rows.size){
                Text(rows[it])
            }
        }

    }
}