package com.example.moderntexteditor.manager

import android.content.Context
import com.example.moderntexteditor.model.EditorFile
import com.example.moderntexteditor.utils.Constants
import java.io.File

class FileManager(
    private val context: Context
) {

    private val fileDirectory: File by lazy {
        File(
            context.filesDir,
            Constants.FILE_DIRECTORY
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun saveFile(
        fileName: String,
        content: String
    ): String {

        val file = File(fileDirectory, fileName)

        file.writeText(
            content,
            Charsets.UTF_8
        )

        return file.absolutePath
    }

    fun readFile(
        filePath: String
    ): String {

        val file = File(filePath)

        if (!file.exists()) {
            return ""
        }

        return file.readText(
            Charsets.UTF_8
        )
    }

    fun deleteFile(
        filePath: String
    ): Boolean {

        val file = File(filePath)

        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun fileExists(
        filePath: String
    ): Boolean {

        return File(filePath).exists()
    }

    fun getFile(
        filePath: String
    ): File? {

        val file = File(filePath)

        return if (file.exists()) {
            file
        } else {
            null
        }
    }

    fun getAllFiles(): List<File> {

        return fileDirectory
            .listFiles()
            ?.filter { it.isFile }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun createEditorFile(
        fileName: String,
        content: String = ""
    ): EditorFile {

        val path = saveFile(
            fileName,
            content
        )

        val currentTime = System.currentTimeMillis()

        return EditorFile(
            fileName = fileName,
            filePath = path,
            content = content,
            encoding = Constants.DEFAULT_ENCODING,
            isReadOnly = false,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    }

    fun renameFile(
        oldPath: String,
        newFileName: String
    ): String? {

        val oldFile = File(oldPath)

        if (!oldFile.exists()) {
            return null
        }

        val newFile = File(
            fileDirectory,
            newFileName
        )

        return if (oldFile.renameTo(newFile)) {
            newFile.absolutePath
        } else {
            null
        }
    }
}