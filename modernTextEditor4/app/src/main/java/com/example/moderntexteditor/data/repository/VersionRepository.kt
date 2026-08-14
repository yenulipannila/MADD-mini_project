package com.example.moderntexteditor.data.repository

import com.example.moderntexteditor.data.database.VersionDao
import com.example.moderntexteditor.data.entity.VersionEntity
import com.example.moderntexteditor.manager.DiffUtilManager
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VersionRepository(
    private val versionDao: VersionDao,
    private val diffUtilManager: DiffUtilManager
) {

    suspend fun createVersion(
        fileId: Long,
        content: String,
        versionName: String = ""
    ): Long = withContext(Dispatchers.IO) {

        val highestVersion =
            versionDao.getHighestVersion(fileId) ?: 0

        val nextVersion =
            highestVersion + 1

        val contentHash =
            generateHash(content)

        if (nextVersion == 1) {

            val version = VersionEntity(
                fileId = fileId,
                versionNumber = nextVersion,
                versionName = if (
                    versionName.isBlank()
                ) {
                    "Version $nextVersion"
                } else {
                    versionName
                },
                baseContent = content,
                patch = "",
                contentHash = contentHash
            )

            return@withContext versionDao.insert(
                version
            )
        }

        val previousContent =
            reconstructVersion(
                fileId,
                highestVersion
            )

        val patch =
            diffUtilManager.createDiff(
                previousContent,
                content
            )

        val version = VersionEntity(
            fileId = fileId,
            versionNumber = nextVersion,
            versionName = if (
                versionName.isBlank()
            ) {
                "Version $nextVersion"
            } else {
                versionName
            },
            baseContent = "",
            patch = patch,
            contentHash = contentHash
        )

        versionDao.insert(
            version
        )
    }

    suspend fun getVersions(
        fileId: Long
    ): List<VersionEntity> =
        withContext(Dispatchers.IO) {

            versionDao.getVersions(fileId)
        }

    suspend fun getLatestVersion(
        fileId: Long
    ): VersionEntity? =
        withContext(Dispatchers.IO) {

            versionDao.getLatest(fileId)
        }

    suspend fun getVersionContent(
        fileId: Long,
        versionNumber: Int
    ): String = withContext(Dispatchers.IO) {

        reconstructVersion(
            fileId,
            versionNumber
        )
    }

    suspend fun deleteVersion(
        version: VersionEntity
    ) = withContext(Dispatchers.IO) {

        versionDao.delete(
            version
        )
    }

    private suspend fun reconstructVersion(
        fileId: Long,
        targetVersion: Int
    ): String {

        val versions =
            versionDao.getVersions(fileId)

        if (versions.isEmpty()) {
            return ""
        }

        val target =
            versions.firstOrNull {
                it.versionNumber == targetVersion
            } ?: return ""

        var content = target.baseContent

        if (content.isNotEmpty()) {

            return content
        }

        val firstVersion =
            versions.firstOrNull()

        if (firstVersion != null) {

            content =
                firstVersion.baseContent
        }

        for (version in versions) {

            if (
                version.versionNumber == 1
            ) {
                continue
            }

            if (
                version.versionNumber >
                targetVersion
            ) {
                break
            }

            content =
                diffUtilManager.applyDiff(
                    content,
                    version.patch
                )
        }

        return content
    }

    private fun generateHash(
        content: String
    ): String {

        val digest =
            MessageDigest.getInstance("SHA-256")

        val hashBytes =
            digest.digest(
                content.toByteArray(
                    Charsets.UTF_8
                )
            )

        return hashBytes.joinToString("") {
            "%02x".format(it)
        }
    }
}