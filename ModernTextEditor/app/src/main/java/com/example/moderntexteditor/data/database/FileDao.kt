package com.example.moderntexteditor.data.database

import androidx.room.*
import com.example.moderntexteditor.data.entity.FileEntity

@Dao
interface FileDao {

    @Insert
    suspend fun insert(file: FileEntity): Long

    @Update
    suspend fun update(file: FileEntity)

    @Delete
    suspend fun delete(file: FileEntity)

    @Query("SELECT * FROM files ORDER BY updatedAt DESC")
    suspend fun getAllFiles(): List<FileEntity>

    @Query("SELECT * FROM files WHERE id=:id")
    suspend fun getFileById(id: Long): FileEntity?

    @Query("SELECT * FROM files WHERE fileName=:name LIMIT 1")
    suspend fun getFileByName(name: String): FileEntity?

    @Query("UPDATE files SET isReadOnly=:value WHERE id=:id")
    suspend fun setReadOnly(id: Long, value: Boolean)

    @Query("UPDATE files SET updatedAt=:time WHERE id=:id")
    suspend fun updateTime(id: Long, time: Long)
}