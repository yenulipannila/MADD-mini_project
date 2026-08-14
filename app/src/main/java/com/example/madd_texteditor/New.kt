package com.example.madd_texteditor

import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.snapshots.SnapshotStateList

fun createNewFile(
    currentText: String,
    onTextChange: (String) -> Unit,
    onFileNameChange: (String) -> Unit,
    onUriChange: (Uri?) -> Unit,
    undoStack: SnapshotStateList<String>,
    redoStack: SnapshotStateList<String>
) {

    if (currentText.isNotEmpty()) {
        undoStack.add(currentText)
    }

    onTextChange("")
    onFileNameChange("Untitled.txt")
    onUriChange(null)

    redoStack.clear()
}