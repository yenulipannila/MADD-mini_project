package com.example.madd_texteditor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RollbackButtons(
    text: String,
    onTextChange: (String) -> Unit,
    undoStack: SnapshotStateList<String>,
    redoStack: SnapshotStateList<String>
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // =================================================
        // UNDO
        // =================================================

        Button(

            onClick = {

                if (undoStack.isNotEmpty()) {

                    // Save current text for redo
                    redoStack.add(text)

                    // Get previous text
                    val previousText =
                        undoStack.removeAt(
                            undoStack.lastIndex
                        )

                    onTextChange(previousText)
                }
            },

            enabled = undoStack.isNotEmpty()

        ) {

            Text("↩ Undo")
        }

        // =================================================
        // REDO
        // =================================================

        Button(

            onClick = {

                if (redoStack.isNotEmpty()) {

                    // Save current text for undo
                    undoStack.add(text)

                    // Get next text
                    val nextText =
                        redoStack.removeAt(
                            redoStack.lastIndex
                        )

                    onTextChange(nextText)
                }
            },

            enabled = redoStack.isNotEmpty()

        ) {

            Text("↪ Redo")
        }
    }
}