package com.example.moderntexteditor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun CodeEditor(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fileType: String = "kotlin",
    wordWrap: Boolean = true
) {
    val editorState = rememberTextFieldState(initialText = text)

    // Keep state in sync when external text changes
    LaunchedEffect(text) {
        if (editorState.text.toString() != text) {
            editorState.edit {
                replace(0, length, text)
            }
        }
    }

    // Send state changes back to EditorScreen
    LaunchedEffect(editorState.text) {
        val currentText = editorState.text.toString()
        if (currentText != text) {
            onTextChange(currentText)
        }
    }

    // Generate highlighted text
    val highlightedText = when (fileType.lowercase()) {
        "markdown", "md" -> SyntaxHighlighter.highlightMarkdown(editorState.text.toString())
        else -> SyntaxHighlighter.highlightKotlin(editorState.text.toString())
    }

    val editorTextStyle = TextStyle(
        fontFamily = FontFamily.Monospace, // Ensures precise character width matching
        fontSize = 15.sp,
        lineHeight = 20.sp
    )

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Render highlighted text behind
        Text(
            text = highlightedText,
            modifier = Modifier.fillMaxSize(),
            style = editorTextStyle,
            softWrap = wordWrap
        )

        // Editable transparent text field overlay
        BasicTextField(
            state = editorState,
            modifier = Modifier.fillMaxSize(),
            textStyle = editorTextStyle.copy(color = Color.Transparent),
            lineLimits = TextFieldLineLimits.Default
        )
    }
}