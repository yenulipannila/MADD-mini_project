package com.example.moderntexteditor.file

import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
fun NewButton(
    currentText: String,
    onTextChange: (String) -> Unit,
    onFileNameChange: (String) -> Unit,
    onUriChange: (Uri?) -> Unit,
    undoStack: SnapshotStateList<String>,
    redoStack: SnapshotStateList<String>
) {

    Button(
        onClick = {

            if (currentText.isNotEmpty()) {
                undoStack.add(currentText)
            }

            onTextChange("")
            onFileNameChange("Untitled.txt")
            onUriChange(null)

            redoStack.clear()
        }
    ) {
        Text("New")
    }
}