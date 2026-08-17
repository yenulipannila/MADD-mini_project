package com.example.moderntexteditor.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object SyntaxHighlighter {
    private val kotlinKeywords = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for",
        "fun", "if", "in", "interface", "is", "null", "object", "package",
        "return", "super", "this", "throw", "true", "try", "typealias",
        "val", "var", "when", "while"
    )

    fun highlightKotlin(code: String): AnnotatedString {
        return buildAnnotatedString {
            var index = 0

            while (index < code.length) {
                // Comments
                if (code.startsWith("//", index)) {
                    val end = code.indexOf('\n', index)
                    val commentEnd = if (end == -1) code.length else end
                    withStyle(SpanStyle(color = Color(0xFF6A9955))) {
                        append(code.substring(index, commentEnd))
                    }
                    index = commentEnd
                    continue
                }

                // Strings
                if (code[index] == '"') {
                    var end = index + 1
                    while (end < code.length && code[end] != '"') {
                        end++
                    }
                    if (end < code.length) end++

                    withStyle(SpanStyle(color = Color(0xFFCE9178))) {
                        append(code.substring(index, end))
                    }
                    index = end
                    continue
                }

                // Annotations
                if (code[index] == '@') {
                    var end = index + 1
                    while (end < code.length && (code[end].isLetterOrDigit() || code[end] == '_')) {
                        end++
                    }
                    withStyle(SpanStyle(color = Color(0xFFDCDCAA), fontWeight = FontWeight.Bold)) {
                        append(code.substring(index, end))
                    }
                    index = end
                    continue
                }

                // Keywords / Identifiers
                if (code[index].isLetter() || code[index] == '_') {
                    var end = index + 1
                    while (end < code.length && (code[end].isLetterOrDigit() || code[end] == '_')) {
                        end++
                    }
                    val word = code.substring(index, end)
                    if (word in kotlinKeywords) {
                        withStyle(SpanStyle(color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)) {
                            append(word)
                        }
                    } else {
                        append(word)
                    }
                    index = end
                    continue
                }

                append(code[index])
                index++
            }
        }
    }

    fun highlightMarkdown(markdown: String): AnnotatedString {
        return buildAnnotatedString {
            val lines = markdown.split("\n")

            lines.forEachIndexed { index, line ->
                when {
                    line.startsWith("#") -> {
                        withStyle(SpanStyle(color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)) {
                            append(line)
                        }
                    }

                    line.contains("**") -> {
                        var currentIndex = 0
                        val regex = Regex("\\*\\*(.*?)\\*\\*")
                        regex.findAll(line).forEach { match ->
                            append(line.substring(currentIndex, match.range.first))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))) {
                                append(match.value)
                            }
                            currentIndex = match.range.last + 1
                        }
                        if (currentIndex < line.length) {
                            append(line.substring(currentIndex))
                        }
                    }

                    line.contains("*") -> {
                        var currentIndex = 0
                        val regex = Regex("\\*(.*?)\\*")
                        regex.findAll(line).forEach { match ->
                            append(line.substring(currentIndex, match.range.first))
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF00897B))) {
                                append(match.value)
                            }
                            currentIndex = match.range.last + 1
                        }
                        if (currentIndex < line.length) {
                            append(line.substring(currentIndex))
                        }
                    }

                    line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ") -> {
                        withStyle(SpanStyle(color = Color(0xFFE65100))) {
                            append(line)
                        }
                    }

                    line.contains("`") -> {
                        withStyle(SpanStyle(color = Color(0xFF795548))) {
                            append(line)
                        }
                    }

                    else -> append(line)
                }

                if (index < lines.lastIndex) {
                    append("\n")
                }
            }
        }
    }
}