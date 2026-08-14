package com.example.moderntexteditor.file

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun SaveButton(
    text: String,
    fileName: String,
    onFileSaved: (Uri, String) -> Unit
) {

    val context = LocalContext.current

    val saveFileLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(
                "text/plain"
            )
        ) { uri: Uri? ->

            uri?.let {

                try {

                    context.contentResolver
                        .openOutputStream(it)
                        ?.use { outputStream ->

                            outputStream.write(
                                text.toByteArray()
                            )
                        }

                    val savedFileName =
                        it.lastPathSegment
                            ?: fileName

                    onFileSaved(
                        it,
                        savedFileName
                    )

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }

    Button(
        onClick = {

            saveFileLauncher.launch(fileName)

        }
    ) {

        Text("Save")
    }
}