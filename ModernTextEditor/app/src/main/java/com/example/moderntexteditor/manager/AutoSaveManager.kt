package com.example.moderntexteditor.manager

import android.content.Context
import com.example.moderntexteditor.utils.Constants
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AutoSaveManager(
    private val context: Context
) {

    private val autoSaveDirectory: File by lazy {

        File(
            context.filesDir,
            Constants.AUTOSAVE_DIRECTORY
        ).apply {

            if (!exists()) {
                mkdirs()
            }
        }
    }

    suspend fun autoSave(
        fileId: Long,
        content: String
    ): String = withContext(Dispatchers.IO) {

        val file = File(
            autoSaveDirectory,
            "$fileId${Constants.AUTOSAVE_EXTENSION}"
        )

        file.writeText(
            content,
            Charsets.UTF_8
        )

        file.absolutePath
    }

    suspend fun getAutoSavedContent(
        fileId: Long
    ): String? = withContext(Dispatchers.IO) {

        val file = File(
            autoSaveDirectory,
            "$fileId${Constants.AUTOSAVE_EXTENSION}"
        )

        if (file.exists()) {
            file.readText(Charsets.UTF_8)
        } else {
            null
        }
    }

    suspend fun hasAutoSave(
        fileId: Long
    ): Boolean = withContext(Dispatchers.IO) {

        val file = File(
            autoSaveDirectory,
            "$fileId${Constants.AUTOSAVE_EXTENSION}"
        )

        file.exists()
    }

    suspend fun deleteAutoSave(
        fileId: Long
    ): Boolean = withContext(Dispatchers.IO) {

        val file = File(
            autoSaveDirectory,
            "$fileId${Constants.AUTOSAVE_EXTENSION}"
        )

        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    suspend fun clearAllAutoSaves() =
        withContext(Dispatchers.IO) {

            autoSaveDirectory
                .listFiles()
                ?.filter {
                    it.name.endsWith(
                        Constants.AUTOSAVE_EXTENSION
                    )
                }
                ?.forEach {
                    it.delete()
                }
        }
}