package com.example.moderntexteditor.editor

object CodeFormatter {

    fun formatKotlin(code: String): String {
        if (code.isBlank()) return code

        val lines = code.lines()
        val formatted = StringBuilder()
        var indentationLevel = 0

        for (line in lines) {
            val trimmedLine = line.trim()

            if (trimmedLine.isEmpty()) {
                formatted.append("\n")
                continue
            }

            if (trimmedLine.startsWith("}")) {
                indentationLevel = maxOf(0, indentationLevel - 1)
            }

            repeat(indentationLevel) {
                formatted.append("    ")
            }

            formatted.append(trimmedLine).append("\n")

            if (trimmedLine.endsWith("{")) {
                indentationLevel++
            }
        }

        return formatted.toString().trimEnd()
    }
}