package com.example.moderntexteditor.file

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// Shown when CrashRecoveryManager detects that the last auto-saved buffer
// for a file differs from the file's own last saved content — i.e. the app
// most likely closed (crashed / was killed) before the user could Save.
@Composable
fun CrashRecoveryDialog(
    onRestore: () -> Unit,
    onDiscard: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDiscard,
        title = {
            Text("Unsaved changes found")
        },
        text = {
            Text(
                "It looks like the app closed before your last edits to " +
                    "this file were saved. An auto-saved copy from your " +
                    "previous session is available. Restore it?"
            )
        },
        confirmButton = {
            TextButton(
                onClick = onRestore
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDiscard
            ) {
                Text("Discard")
            }
        }
    )
}
