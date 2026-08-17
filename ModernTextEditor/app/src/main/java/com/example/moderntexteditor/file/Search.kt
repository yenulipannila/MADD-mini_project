package com.example.moderntexteditor.file

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Search(
    text: String,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {

    var searchResult by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(

            value = searchText,

            onValueChange = {

                onSearchTextChange(it)
                searchResult = ""
            },

            modifier = Modifier.weight(1f),

            placeholder = {
                Text("Search text...")
            },

            singleLine = true
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Button(
            onClick = {

                if (searchText.isBlank()) {

                    searchResult =
                        "Enter text to search."

                } else {

                    val position =
                        text.indexOf(
                            searchText,
                            ignoreCase = true
                        )

                    searchResult =
                        if (position >= 0) {

                            "Found at position ${position + 1}"

                        } else {

                            "Text not found."
                        }
                }
            }
        ) {

            Text("Search")
        }
    }

    if (searchResult.isNotEmpty()) {

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = searchResult
        )
    }
}