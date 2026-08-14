package com.example.moderntexteditor.file

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moderntexteditor.editor.CodeEditor
import com.example.moderntexteditor.editor.CodeFormatter
import com.example.moderntexteditor.editor.MarkdownPreview

@Composable
fun TextEditor() {
    //displays the code the editor types
    // FILE STATE
    var text by remember {
        mutableStateOf("")
    }

    var fileName by remember {
        mutableStateOf("Untitled.kt")
    }

    var currentFileUri by remember {
        mutableStateOf<Uri?>(null)
    }

    // SEARCH / REPLACE STATE
    var searchText by remember {
        mutableStateOf("")
    }

    var replaceText by remember {
        mutableStateOf("")
    }

    // UNDO / REDO
    val undoStack = remember {
        mutableStateListOf<String>()
    }

    val redoStack = remember {
        mutableStateListOf<String>()
    }

    // RECENT FILES
    val recentFiles = remember {
        mutableStateListOf<String>()
    }

    // EDITOR SETTINGS
    var wordWrap by remember {
        mutableStateOf(true)
    }

    var showPreview by remember {
        mutableStateOf(false)
    }

    // DETERMINE FILE TYPE
    val fileType = when {
        fileName.endsWith(".md", ignoreCase = true) -> "markdown"
        fileName.endsWith(".markdown", ignoreCase = true) -> "markdown"
        else -> "kotlin"
    }

    // MAIN SCREEN
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Modern Text Editor",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // NEW / OPEN / SAVE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                NewButton(
                    currentText = text,
                    onTextChange = { text = it },
                    onFileNameChange = { fileName = it },
                    onUriChange = { currentFileUri = it },
                    undoStack = undoStack,
                    redoStack = redoStack
                )

                OpenButton(
                    onFileOpened = { openedText, uri, name ->
                        text = openedText
                        currentFileUri = uri
                        fileName = name
                    }
                )

                SaveButton(
                    text = text,
                    fileName = fileName,
                    onFileSaved = { uri, name ->
                        currentFileUri = uri
                        fileName = name
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // EDITOR CONTROLS
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                // WORD WRAP + FORMAT CODE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Word Wrap",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Switch(
                            checked = wordWrap,
                            onCheckedChange = {
                                wordWrap = it
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (fileType == "kotlin") {
                                text = CodeFormatter.formatKotlin(text)
                            }
                        }
                    ) {
                        Text("Format Code")
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // EDITOR / PREVIEW
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(
                        selected = !showPreview,
                        onClick = {
                            showPreview = false
                        },
                        label = {
                            Text("Editor")
                        }
                    )

                    FilterChip(
                        selected = showPreview,
                        onClick = {
                            showPreview = true
                        },
                        label = {
                            Text("Preview")
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // SEARCH & REPLACE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = "Find & Replace",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                SearchSection(
                    text = text,
                    searchText = searchText,
                    onSearchTextChange = {
                        searchText = it
                    }
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                ReplaceSection(
                    text = text,
                    searchText = searchText,
                    replaceText = replaceText,
                    onReplaceTextChange = {
                        replaceText = it
                    },
                    onTextChange = {
                        text = it
                    },
                    undoStack = undoStack,
                    redoStack = redoStack
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // UNDO / REDO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            RollbackButtons(
                text = text,
                onTextChange = {
                    text = it
                },
                undoStack = undoStack,
                redoStack = redoStack
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // EDITOR TITLE
        Text(
            text = "Editor",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // CODE EDITOR / MARKDOWN PREVIEW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            if (showPreview) {

                MarkdownPreview(
                    markdown = text,
                    modifier = Modifier.fillMaxSize()
                )

            } else {

                CodeEditor(
                    text = text,
                    onTextChange = {

                        if (it != text) {
                            undoStack.add(text)
                        }

                        text = it
                        redoStack.clear()
                    },
                    modifier = Modifier.fillMaxSize(),
                    fileType = fileType,
                    wordWrap = wordWrap
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // RECENT FILES
        if (recentFiles.isNotEmpty()) {

            Text(
                text = "Recent Files",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            RecentFiles(
                recentFiles = recentFiles,
                onFileSelected = { openedText, uri, name ->
                    text = openedText
                    currentFileUri = uri
                    fileName = name
                }
            )
        }
    }
}