package com.example.madd_texteditor

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun RecentFiles(
    recentFiles: List<String>,
    onFileSelected: (String, Uri, String) -> Unit
) {

    val context = LocalContext.current

    if (recentFiles.isNotEmpty()) {

        Text(
            text = "Recent Files",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 150.dp)
        ) {

            items(recentFiles) { uriString ->

                Button(

                    onClick = {

                        try {

                            val uri =
                                Uri.parse(uriString)

                            val inputStream =
                                context.contentResolver
                                    .openInputStream(uri)

                            val reader =
                                BufferedReader(
                                    InputStreamReader(
                                        inputStream
                                    )
                                )

                            val openedText =
                                reader.readText()

                            reader.close()

                            val fileName =
                                uri.lastPathSegment
                                    ?: "RecentFile.txt"

                            onFileSelected(
                                openedText,
                                uri,
                                fileName
                            )

                        } catch (e: Exception) {

                            e.printStackTrace()
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)

                ) {

                    Text(
                        text =
                            Uri.parse(uriString)
                                .lastPathSegment
                                ?: "Recent File"
                    )
                }
            }
        }
    }
}