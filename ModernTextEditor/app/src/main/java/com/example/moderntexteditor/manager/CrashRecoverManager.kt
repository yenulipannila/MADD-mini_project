package com.example.moderntexteditor.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CrashRecoveryManager(
    private val autoSaveManager: AutoSaveManager
) {

    suspend fun checkForRecovery(
        fileId: Long,
        currentContent: String
    ): RecoveryResult = withContext(Dispatchers.IO) {

        val autoSavedContent =
            autoSaveManager.getAutoSavedContent(fileId)

        if (autoSavedContent == null) {

            RecoveryResult(
                hasRecovery = false,
                content = currentContent
            )

        } else {

            if (autoSavedContent != currentContent) {

                RecoveryResult(
                    hasRecovery = true,
                    content = autoSavedContent
                )

            } else {

                RecoveryResult(
                    hasRecovery = false,
                    content = currentContent
                )
            }
        }
    }

    suspend fun recover(
        fileId: Long
    ): String? {

        return autoSaveManager.getAutoSavedContent(
            fileId
        )
    }

    suspend fun discardRecovery(
        fileId: Long
    ): Boolean {

        return autoSaveManager.deleteAutoSave(
            fileId
        )
    }

    data class RecoveryResult(
        val hasRecovery: Boolean,
        val content: String
    )
}