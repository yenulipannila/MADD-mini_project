package com.example.moderntexteditor.file

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun OpenButton(
    onFileOpened: (String, Uri, String) -> Unit
) {

    val context = LocalContext.current

    val openFileLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            uri?.let {

                try {

                    val inputStream =
                        context.contentResolver
                            .openInputStream(it)

                    val reader =
                        BufferedReader(
                            InputStreamReader(inputStream)
                        )

                    val openedText =
                        reader.readText()

                    reader.close()

                    val fileName =
                        it.lastPathSegment
                            ?: "OpenedFile.txt"

                    onFileOpened(
                        openedText,
                        it,
                        fileName
                    )

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }

    Button(
        onClick = {

            openFileLauncher.launch(
                arrayOf(
                    "text/plain",
                    "text/*"
                )
            )
        }
    ) {

        Text("Open")
    }
}