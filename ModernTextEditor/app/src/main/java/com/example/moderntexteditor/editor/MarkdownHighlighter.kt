package com.example.moderntexteditor.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Colors used for Markdown syntax highlighting.
data class MarkdownColors(
    val textColor: Color = Color(0xFF1F2937),
    val codeColor: Color = Color(0xFFD97706),
    val codeBackground: Color = Color(0xFFF3F4F6),
    val linkColor: Color = Color(0xFF2563EB),
    val headerColor: Color = Color(0xFF111827),
    val quoteColor: Color = Color(0xFF6B7280),
    val listColor: Color = Color(0xFF059669)
)

// Markdown syntax highlighter
// Converts Markdown text into an AnnotatedString that can be displayed by the Compose editor.
object MarkdownHighlighter {

    fun highlight(
        text: String,
        colors: MarkdownColors = MarkdownColors()
    ): AnnotatedString = buildAnnotatedString {

        // DEFAULT TEXT
        append(text)

        if (text.isEmpty()) {
            return@buildAnnotatedString
        }

        addStyle(
            style = SpanStyle(
                color = colors.textColor,
                fontSize = 16.sp
            ),
            start = 0,
            end = text.length
        )

        // 1. HEADERS
        // # Header (h1)
        // ## Header (h2)
        // ### Header

        val headerRegex = Regex(
            "(?m)^(#{1,6})\\s+(.+)$"
        )

        for (match in headerRegex.findAll(text)) {

            val level = match.groupValues[1].length

            val contentStart =
                match.groups[2]?.range?.first ?: continue

            val contentEnd =
                match.groups[2]?.range?.last?.plus(1)
                    ?: continue

            val fontSize = when (level) {
                1 -> 26.sp
                2 -> 23.sp
                3 -> 20.sp
                4 -> 18.sp
                5 -> 17.sp
                else -> 16.sp
            }

            addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize,
                    color = colors.headerColor
                ),
                start = contentStart,
                end = contentEnd
            )
        }

        // 2. BOLD
        // **bold**
        // __bold__
        val boldRegex = Regex(
            "(\\*\\*|__)(.+?)\\1"
        )

        for (match in boldRegex.findAll(text)) {

            val contentStart =
                match.groups[2]?.range?.first ?: continue

            val contentEnd =
                match.groups[2]?.range?.last?.plus(1)
                    ?: continue

            addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold
                ),
                start = contentStart,
                end = contentEnd
            )
        }

        // 3. ITALICS
        // *italic*
        // _italic_
        val italicRegex = Regex(
            "(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)|(?<!_)_([^_\\n]+)_(?!_)"
        )

        for (match in italicRegex.findAll(text)) {

            val contentGroup =
                match.groups[1] ?: match.groups[2] ?: continue

            addStyle(
                style = SpanStyle(
                    fontStyle = FontStyle.Italic
                ),
                start = contentGroup.range.first,
                end = contentGroup.range.last + 1
            )
        }

        // 4. INLINE CODE
        // `code`
        val inlineCodeRegex = Regex(
            "`([^`]+)`"
        )

        for (match in inlineCodeRegex.findAll(text)) {

            val contentStart =
                match.groups[1]?.range?.first ?: continue

            val contentEnd =
                match.groups[1]?.range?.last?.plus(1)
                    ?: continue

            addStyle(
                style = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    color = colors.codeColor,
                    background = colors.codeBackground
                ),
                start = contentStart,
                end = contentEnd
            )
        }

        // 5. CODE BLOCKS
        //
        // ```kotlin
        // code
        // ```
        val codeBlockRegex = Regex(
            "```(?:[a-zA-Z0-9_+-]+)?\\n([\\s\\S]*?)```"
        )

        for (match in codeBlockRegex.findAll(text)) {

            val contentStart =
                match.groups[1]?.range?.first ?: continue

            val contentEnd =
                match.groups[1]?.range?.last?.plus(1)
                    ?: continue

            addStyle(
                style = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    color = colors.codeColor,
                    background = colors.codeBackground
                ),
                start = contentStart,
                end = contentEnd
            )
        }

        // 6. LINKS
        // [Google](https://google.com)
        val linkRegex = Regex(
            "\\[([^]]+)]\\(([^)]+)\\)"
        )

        for (match in linkRegex.findAll(text)) {

            val linkTextStart =
                match.groups[1]?.range?.first ?: continue

            val linkTextEnd =
                match.groups[1]?.range?.last?.plus(1)
                    ?: continue

            addStyle(
                style = SpanStyle(
                    color = colors.linkColor,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                ),
                start = linkTextStart,
                end = linkTextEnd
            )
        }

        // 7. BLOCKQUOTES
        // > This is a quote
        val quoteRegex = Regex(
            "(?m)^>\\s?.+$"
        )

        for (match in quoteRegex.findAll(text)) {

            addStyle(
                style = SpanStyle(
                    color = colors.quoteColor,
                    fontStyle = FontStyle.Italic
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }

        // 8. BULLET LISTS
        // - Item
        // * Item
        // + Item
        val bulletRegex = Regex(
            "(?m)^[ \\t]*[-*+]\\s+.+$"
        )

        for (match in bulletRegex.findAll(text)) {

            addStyle(
                style = SpanStyle(
                    color = colors.listColor
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }

        // 9. NUMBERED LISTS
        // 1. First
        // 2. Second
        val numberedListRegex = Regex(
            "(?m)^[ \\t]*\\d+\\.\\s+.+$"
        )

        for (match in numberedListRegex.findAll(text)) {

            addStyle(
                style = SpanStyle(
                    color = colors.listColor
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }

        // 10. HORIZONTAL RULE
        //
        // ---
        // ***
        // ___
        val horizontalRuleRegex = Regex(
            "(?m)^[ \\t]*([-*_])(?:[ \\t]*\\1){2,}[ \\t]*$"
        )

        for (match in horizontalRuleRegex.findAll(text)) {

            addStyle(
                style = SpanStyle(
                    color = colors.quoteColor,
                    fontWeight = FontWeight.Bold
                ),
                start = match.range.first,
                end = match.range.last + 1
            )
        }
    }
}