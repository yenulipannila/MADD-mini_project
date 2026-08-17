package com.example.moderntexteditor.file

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moderntexteditor.data.entity.VersionEntity
import com.example.moderntexteditor.data.repository.VersionRepository
import com.example.moderntexteditor.manager.DiffUtilManager
import kotlinx.coroutines.launch

// Rollback / diff UI for the delta-based version control system.
//
// Reconstructing a version's content is delegated to
// VersionRepository.getVersionContent(), which walks the delta chain
// (Version 1 baseContent + every patch up to the target version) rather
// than storing a full duplicate copy per version.
@Composable
fun VersionHistoryDialog(
    fileId: Long,
    currentText: String,
    versionRepository: VersionRepository,
    diffUtilManager: DiffUtilManager,
    onRestore: (String) -> Unit,
    onDismiss: () -> Unit
) {

    val scope = rememberCoroutineScope()

    var versions by remember {
        mutableStateOf<List<VersionEntity>>(emptyList())
    }

    var selectedVersionNumber by remember {
        mutableStateOf<Int?>(null)
    }

    var diffText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(fileId) {
        versions = versionRepository
            .getVersions(fileId)
            .sortedByDescending {
                it.versionNumber
            }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Version History")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {

                Text(
                    text = "${versions.size} saved version(s). " +
                        "Tap Diff to compare against your current edits, " +
                        "or Restore to roll back to that version.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (versions.isEmpty()) {

                    Text(
                        "No saved versions yet. Press Save to create Version 1."
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                    ) {

                        items(
                            items = versions,
                            key = { it.id }
                        ) { version ->

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = "${version.versionName} " +
                                            "(v${version.versionNumber})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Row {

                                        TextButton(
                                            onClick = {

                                                scope.launch {

                                                    val content =
                                                        versionRepository
                                                            .getVersionContent(
                                                                fileId,
                                                                version.versionNumber
                                                            )

                                                    diffText =
                                                        diffUtilManager.createDiff(
                                                            content,
                                                            currentText
                                                        )

                                                    selectedVersionNumber =
                                                        version.versionNumber
                                                }
                                            }
                                        ) {
                                            Text("Diff")
                                        }

                                        TextButton(
                                            onClick = {

                                                scope.launch {

                                                    val content =
                                                        versionRepository
                                                            .getVersionContent(
                                                                fileId,
                                                                version.versionNumber
                                                            )

                                                    onRestore(content)
                                                }
                                            }
                                        ) {
                                            Text("Restore")
                                        }
                                    }
                                }

                                HorizontalDivider()
                            }
                        }
                    }
                }

                if (selectedVersionNumber != null) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Diff: v$selectedVersionNumber \u2192 current buffer",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    DiffViewer(
                        diff = diffText
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Close")
            }
        }
    )
}

// Renders the "+/-/space" prefixed patch text produced by DiffUtilManager
// as colour-coded lines: green for additions, red for removals, and plain
// text for unchanged context lines.
@Composable
private fun DiffViewer(
    diff: String,
    modifier: Modifier = Modifier
) {

    val lines = diff
        .lines()
        .filter {
            it.isNotEmpty()
        }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 180.dp)
    ) {

        items(lines) { line ->

            val (backgroundColor, foregroundColor) = when {

                line.startsWith("+") ->
                    Color(0xFFDCFCE7) to Color(0xFF166534)

                line.startsWith("-") ->
                    Color(0xFFFEE2E2) to Color(0xFF991B1B)

                else ->
                    Color.Transparent to Color(0xFF374151)
            }

            Text(
                text = line,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = foregroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(
                        horizontal = 4.dp,
                        vertical = 1.dp
                    )
            )
        }
    }
}
