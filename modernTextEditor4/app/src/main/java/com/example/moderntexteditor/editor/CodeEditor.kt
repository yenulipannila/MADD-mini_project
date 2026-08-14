package com.example.moderntexteditor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodeEditor(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fileType: String = "kotlin",
    wordWrap: Boolean = true
) {

    val horizontalScrollState = rememberScrollState()

    // Choose the correct syntax highlighter
    val visualTransformation = remember(
        text,
        fileType
    ) {

        VisualTransformation { currentText ->

            val highlightedText: AnnotatedString =
                when (fileType.lowercase()) {

                    // Markdown files
                    "markdown" -> {
                        SyntaxHighlighter.highlightMarkdown(
                            currentText.text
                        )
                    }

                    // Kotlin files
                    "kotlin" -> {
                        SyntaxHighlighter.highlightKotlin(
                            currentText.text
                        )
                    }

                    // Other file types
                    else -> {
                        AnnotatedString(
                            currentText.text
                        )
                    }
                }

            TransformedText(
                text = highlightedText,
                offsetMapping = OffsetMapping.Identity
            )
        }
    }

    val editorModifier =
        if (wordWrap) {

            modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .padding(12.dp)

        } else {

            modifier
                .fillMaxSize()
                .horizontalScroll(
                    horizontalScrollState
                )
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .padding(12.dp)
        }

    Box(
        modifier = editorModifier
    ) {

        BasicTextField(
            value = text,

            onValueChange = { newText ->
                onTextChange(newText)
            },

            modifier = Modifier.fillMaxSize(),

            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                color = Color(0xFF1F2937)
            ),

            visualTransformation = visualTransformation,

            singleLine = false,

            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Actual text editor
                    innerTextField()
                }
            }
        )
    }
}