package com.example.moderntexteditor.manager

class DiffUtilManager {

    fun createDiff(
        oldContent: String,
        newContent: String
    ): String {

        val oldLines = oldContent.lines()
        val newLines = newContent.lines()

        val result = StringBuilder()

        var oldIndex = 0
        var newIndex = 0

        while (
            oldIndex < oldLines.size ||
            newIndex < newLines.size
        ) {

            if (
                oldIndex < oldLines.size &&
                newIndex < newLines.size &&
                oldLines[oldIndex] == newLines[newIndex]
            ) {

                result.append(
                    " ${oldLines[oldIndex]}\n"
                )

                oldIndex++
                newIndex++

            } else {

                if (oldIndex < oldLines.size) {

                    result.append(
                        "-${oldLines[oldIndex]}\n"
                    )

                    oldIndex++
                }

                if (newIndex < newLines.size) {

                    result.append(
                        "+${newLines[newIndex]}\n"
                    )

                    newIndex++
                }
            }
        }

        return result.toString()
    }

    fun applyDiff(
        oldContent: String,
        patch: String
    ): String {

        val result = StringBuilder()

        val patchLines = patch.lines()

        for (line in patchLines) {

            if (line.isEmpty()) {
                continue
            }

            when (line.first()) {

                ' ' -> {
                    result.append(
                        line.substring(1)
                    )
                    result.append("\n")
                }

                '+' -> {
                    result.append(
                        line.substring(1)
                    )
                    result.append("\n")
                }

                '-' -> {
                    // Removed line.
                    // Do not add it.
                }
            }
        }

        return result.toString().trimEnd('\n')
    }

    fun hasChanges(
        oldContent: String,
        newContent: String
    ): Boolean {

        return oldContent != newContent
    }
}