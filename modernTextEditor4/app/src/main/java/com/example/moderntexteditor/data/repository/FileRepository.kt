package com.example.moderntexteditor.data.repository

import com.example.moderntexteditor.data.database.FileDao
import com.example.moderntexteditor.data.entity.FileEntity
import com.example.moderntexteditor.manager.FileManager
import com.example.moderntexteditor.model.EditorFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileRepository(
    private val fileDao: FileDao,
    private val fileManager: FileManager
) {

    suspend fun createFile(
        fileName: String,
        content: String = ""
    ): EditorFile = withContext(Dispatchers.IO) {

        val path = fileManager.saveFile(
            fileName,
            content
        )

        val currentTime = System.currentTimeMillis()

        val entity = FileEntity(
            fileName = fileName,
            filePath = path,
            encoding = "UTF-8",
            isReadOnly = false,
            createdAt = currentTime,
            updatedAt = currentTime
        )

        val id = fileDao.insert(entity)

        EditorFile(
            id = id,
            fileName = fileName,
            filePath = path,
            content = content,
            encoding = entity.encoding,
            isReadOnly = entity.isReadOnly,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    suspend fun saveFile(
        fileId: Long,
        content: String
    ): Boolean = withContext(Dispatchers.IO) {

        val file = fileDao.getFileById(fileId)
            ?: return@withContext false

        if (file.isReadOnly) {
            return@withContext false
        }

        fileManager.saveFile(
            file.fileName,
            content
        )

        fileDao.updateTime(
            fileId,
            System.currentTimeMillis()
        )

        true
    }

    suspend fun getFile(
        fileId: Long
    ): EditorFile? = withContext(Dispatchers.IO) {

        val entity = fileDao.getFileById(fileId)
            ?: return@withContext null

        val content = fileManager.readFile(
            entity.filePath
        )

        entity.toEditorFile(content)
    }

    suspend fun getAllFiles(): List<EditorFile> =
        withContext(Dispatchers.IO) {

            fileDao
                .getAllFiles()
                .map { entity ->

                    val content =
                        fileManager.readFile(
                            entity.filePath
                        )

                    entity.toEditorFile(content)
                }
        }

    suspend fun deleteFile(
        fileId: Long
    ): Boolean = withContext(Dispatchers.IO) {

        val entity =
            fileDao.getFileById(fileId)
                ?: return@withContext false

        fileManager.deleteFile(
            entity.filePath
        )

        fileDao.delete(entity)

        true
    }

    suspend fun renameFile(
        fileId: Long,
        newFileName: String
    ): Boolean = withContext(Dispatchers.IO) {

        val entity =
            fileDao.getFileById(fileId)
                ?: return@withContext false

        val newPath =
            fileManager.renameFile(
                entity.filePath,
                newFileName
            ) ?: return@withContext false

        val updatedEntity = entity.copy(
            fileName = newFileName,
            filePath = newPath,
            updatedAt = System.currentTimeMillis()
        )

        fileDao.update(
            updatedEntity
        )

        true
    }

    suspend fun setReadOnly(
        fileId: Long,
        readOnly: Boolean
    ) = withContext(Dispatchers.IO) {

        fileDao.setReadOnly(
            fileId,
            readOnly
        )
    }

    suspend fun findByName(
        fileName: String
    ): EditorFile? = withContext(Dispatchers.IO) {

        val entity =
            fileDao.getFileByName(fileName)
                ?: return@withContext null

        val content =
            fileManager.readFile(
                entity.filePath
            )

        entity.toEditorFile(content)
    }

    private fun FileEntity.toEditorFile(
        content: String
    ): EditorFile {

        return EditorFile(
            id = id,
            fileName = fileName,
            filePath = filePath,
            content = content,
            encoding = encoding,
            isReadOnly = isReadOnly,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}