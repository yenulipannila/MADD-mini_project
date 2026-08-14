package com.example.moderntexteditor.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditorScreen() {

    var fileName by remember { mutableStateOf("README.md") }
    var wordWrap by remember { mutableStateOf(true) }
    var showPreview by remember { mutableStateOf(false) }

    var text by remember {
        mutableStateOf(
            """
            # Modern Text Editor

            ## Markdown Preview

            **Bold text**

            *Italic text*

            - First item
            - Second item

            `inline code`
            """.trimIndent()
        )
    }

    val fileType = when {
        fileName.endsWith(".md", ignoreCase = true) -> "markdown"
        fileName.endsWith(".markdown", ignoreCase = true) -> "markdown"
        else -> "kotlin"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header / Title Bar
        Text(
            text = "Modern Text Editor",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Control Panel Surface
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                // Row 1: Word Wrap Switch + Format Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Word Wrap", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = wordWrap,
                            onCheckedChange = { wordWrap = it },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = { text = CodeFormatter.formatKotlin(text) }
                    ) {
                        Text("Format Code")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Mode Toggle (Editor vs Preview)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !showPreview,
                        onClick = { showPreview = false },
                        label = { Text("Editor") }
                    )
                    FilterChip(
                        selected = showPreview,
                        onClick = { showPreview = true },
                        label = { Text("Preview") }
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(bottom = 8.dp))

        // Main Workspace (Editor or Preview)
        Box(modifier = Modifier.weight(1f)) {
            if (showPreview) {
                MarkdownPreview(
                    markdown = text,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CodeEditor(
                    text = text,
                    onTextChange = { text = it },
                    modifier = Modifier.fillMaxSize(),
                    fileType = fileType,
                    wordWrap = wordWrap
                )
            }
        }
    }
}