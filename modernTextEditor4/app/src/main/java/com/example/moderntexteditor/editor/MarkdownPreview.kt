package com.example.moderntexteditor.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownPreview(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp) // Adds vertical breathing room
    ) {
        val lines = markdown.split("\n")

        lines.forEach { line ->
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = line.removePrefix("# "),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                line.startsWith("## ") -> {
                    Text(
                        text = line.removePrefix("## "),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                line.startsWith("### ") -> {
                    Text(
                        text = line.removePrefix("### "),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                line.startsWith("- ") -> {
                    Text(
                        text = "  •  " + line.removePrefix("- "),
                        fontSize = 15.sp
                    )
                }

                Regex("^\\d+\\. ").containsMatchIn(line) -> {
                    Text(
                        text = line,
                        fontSize = 15.sp
                    )
                }

                line.isBlank() -> {
                    // Empty space for paragraph breaks
                }

                else -> {
                    Text(
                        text = parseInlineMarkdown(line),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

fun parseInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var index = 0

        while (index < text.length) {

            // Bold (**text**)
            if (text.startsWith("**", index)) {
                val end = text.indexOf("**", index + 2)
                if (end != -1) {
                    val content = text.substring(index + 2, end)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(content)
                    }
                    index = end + 2
                    continue
                }
            }

            // Italic (*text*)
            if (text[index] == '*') {
                val end = text.indexOf("*", index + 1)
                if (end != -1) {
                    val content = text.substring(index + 1, end)
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(content)
                    }
                    index = end + 1
                    continue
                }
            }

            // Inline code (`text`) - FIXED STYLING
            if (text[index] == '`') {
                val end = text.indexOf("`", index + 1)
                if (end != -1) {
                    val content = text.substring(index + 1, end)
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0xFFE8E8E8), // Light highlight pill
                            color = Color(0xFFC7254E),       // Classic markdown code tint
                            fontSize = 14.sp
                        )
                    ) {
                        append(" $content ")
                    }
                    index = end + 1
                    continue
                }
            }

            append(text[index])
            index++
        }
    }
}