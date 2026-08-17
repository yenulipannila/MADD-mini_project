package com.example.moderntexteditor.storage

import android.content.Context
import com.example.moderntexteditor.utils.Constants
import java.io.File

class FileManager(
    private val context: Context
) {

    private val filesDirectory: File =
        File(
            context.filesDir,
            Constants.FILE_DIRECTORY
        )

    init {
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs()
        }
    }

    fun createFile(
        fileName: String
    ): File {

        val file =
            File(
                filesDirectory,
                fileName
            )

        if (!file.exists()) {
            file.createNewFile()
        }

        return file
    }

    fun saveFile(
        fileName: String,
        content: String
    ): Boolean {

        return try {

            val file =
                File(
                    filesDirectory,
                    fileName
                )

            file.writeText(
                content,
                Charsets.UTF_8
            )

            true

        } catch (e: Exception) {

            e.printStackTrace()

            false
        }
    }

    fun readFile(
        fileName: String
    ): String? {

        return try {

            val file =
                File(
                    filesDirectory,
                    fileName
                )

            if (file.exists()) {

                file.readText(
                    Charsets.UTF_8
                )

            } else {
                null
            }

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }

    fun deleteFile(
        fileName: String
    ): Boolean {

        val file =
            File(
                filesDirectory,
                fileName
            )

        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun renameFile(
        oldName: String,
        newName: String
    ): Boolean {

        val oldFile =
            File(
                filesDirectory,
                oldName
            )

        val newFile =
            File(
                filesDirectory,
                newName
            )

        return oldFile.exists() &&
                oldFile.renameTo(newFile)
    }

    fun fileExists(
        fileName: String
    ): Boolean {

        return File(
            filesDirectory,
            fileName
        ).exists()
    }

    fun getAllFiles(): List<File> {

        return filesDirectory
            .listFiles()
            ?.filter {
                it.isFile
            }
            ?.sortedByDescending {
                it.lastModified()
            }
            ?: emptyList()
    }

    fun getFilesDirectory(): File {
        return filesDirectory
    }
}