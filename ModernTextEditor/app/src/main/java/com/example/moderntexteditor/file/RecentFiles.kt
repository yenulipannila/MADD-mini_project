package com.example.moderntexteditor.file

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moderntexteditor.model.EditorFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Recent Files sidebar.
// Backed by Room (via FileRepository.getAllFiles()) instead of raw URI
// strings, so previously-opened / previously-saved files persist across
// app restarts and can be reopened with their version history intact.
@Composable
fun RecentFiles(
    files: List<EditorFile>,
    currentFileId: Long?,
    onFileSelected: (EditorFile) -> Unit
) {

    if (files.isEmpty()) {
        return
    }

    val dateFormat = remember {
        SimpleDateFormat(
            "MMM d, HH:mm",
            Locale.getDefault()
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 180.dp)
    ) {

        items(
            items = files,
            key = { it.id }
        ) { file ->

            val isActive = file.id == currentFileId

            Card(
                onClick = {
                    onFileSelected(file)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = file.fileName,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Updated ${dateFormat.format(Date(file.updatedAt))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (file.isReadOnly) {

                        Text(
                            text = "Read-only",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
