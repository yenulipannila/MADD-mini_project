package com.example.moderntexteditor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.foundation.verticalScroll

@Composable
fun CodeEditor(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fileType: String = "kotlin",
    wordWrap: Boolean = true,
    readOnly: Boolean = false
) {

    // --------------------------------------------------------------
    // SCROLL STATES
    // --------------------------------------------------------------

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    // --------------------------------------------------------------
    // SYNTAX HIGHLIGHTING
    // --------------------------------------------------------------

    val visualTransformation = remember(
        text,
        fileType
    ) {

        VisualTransformation { currentText ->

            val highlightedText: AnnotatedString =
                when (fileType.lowercase()) {

                    "markdown" -> {
                        SyntaxHighlighter.highlightMarkdown(
                            currentText.text
                        )
                    }

                    "kotlin" -> {
                        SyntaxHighlighter.highlightKotlin(
                            currentText.text
                        )
                    }

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

    // --------------------------------------------------------------
    // EDITOR BACKGROUND
    // --------------------------------------------------------------

    val backgroundColor =
        if (readOnly) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        }

    // --------------------------------------------------------------
    // MAIN EDITOR
    // --------------------------------------------------------------

    Box(
        modifier = modifier
            .background(backgroundColor)
    ) {

        if (wordWrap) {

            // ==========================================================
            // WORD WRAP ON
            // ==========================================================

            BasicTextField(
                value = text,

                onValueChange = { newText ->
                    onTextChange(newText)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        verticalScrollState
                    )
                    .padding(12.dp),

                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937)
                ),

                visualTransformation =
                    visualTransformation,

                singleLine = false,

                readOnly = readOnly,

                decorationBox = { innerTextField ->

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        innerTextField()
                    }
                }
            )

        } else {

            // ==========================================================
            // WORD WRAP OFF
            // ==========================================================

            Box(
                modifier = Modifier
                    .horizontalScroll(
                        horizontalScrollState
                    )
            ) {

                BasicTextField(
                    value = text,

                    onValueChange = { newText ->
                        onTextChange(newText)
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(
                            verticalScrollState
                        )
                        .padding(12.dp),

                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = Color(0xFF1F2937)
                    ),

                    visualTransformation =
                        visualTransformation,

                    singleLine = false,

                    readOnly = readOnly,

                    decorationBox = { innerTextField ->

                        Box {
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}