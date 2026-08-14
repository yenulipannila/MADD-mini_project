package com.example.madd_texteditor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
fun ReplaceSection(
    text: String,
    searchText: String,
    replaceText: String,
    onReplaceTextChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    undoStack: SnapshotStateList<String>,
    redoStack: SnapshotStateList<String>
) {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(

            value = replaceText,

            onValueChange = {
                onReplaceTextChange(it)
            },

            modifier = Modifier.weight(1f),

            placeholder = {
                Text("Replace with...")
            },

            singleLine = true
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Button(

            onClick = {

                if (searchText.isNotBlank()) {

                    val regex =
                        Regex(
                            Regex.escape(searchText),
                            RegexOption.IGNORE_CASE
                        )

                    val matches =
                        regex.findAll(text).count()

                    if (matches > 0) {

                        // Save current text for rollback
                        undoStack.add(text)

                        val newText =
                            regex.replace(
                                text,
                                replaceText
                            )

                        onTextChange(newText)

                        redoStack.clear()
                    }
                }
            }

        ) {

            Text("Replace")
        }
    }
}