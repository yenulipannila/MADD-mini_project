package com.example.madd_texteditor

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TextEditor() {

    var text by remember {
        mutableStateOf("")
    }

    var fileName by remember {
        mutableStateOf("Untitled.txt")
    }

    var currentFileUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    var replaceText by remember {
        mutableStateOf("")
    }

    val undoStack = remember {
        mutableStateListOf<String>()
    }

    val redoStack = remember {
        mutableStateListOf<String>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        NewButton(
            text = text,
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

        SearchSection(
            text = text,
            searchText = searchText,
            onSearchTextChange = {
                searchText = it
            }
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

        RollbackButtons(
            text = text,
            onTextChange = {
                text = it
            },
            undoStack = undoStack,
            redoStack = redoStack
        )

        OutlinedTextField(
            value = text,
            onValueChange = {
                undoStack.add(text)
                text = it
                redoStack.clear()
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text("Start typing...")
            }
        )

        RecentFiles(
            onFileSelected = { openedText, uri, name ->
                text = openedText
                currentFileUri = uri
                fileName = name
            }
        )
    }
}