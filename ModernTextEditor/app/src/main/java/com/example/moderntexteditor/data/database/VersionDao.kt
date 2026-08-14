package com.example.moderntexteditor.data.database

import androidx.room.*
import com.example.moderntexteditor.data.entity.VersionEntity

@Dao
interface VersionDao {

    @Insert
    suspend fun insert(version: VersionEntity): Long

    @Delete
    suspend fun delete(version: VersionEntity)

    @Query("SELECT * FROM versions WHERE fileId=:fileId ORDER BY versionNumber ASC")
    suspend fun getVersions(fileId: Long): List<VersionEntity>

    @Query("SELECT * FROM versions WHERE fileId=:fileId ORDER BY versionNumber DESC LIMIT 1")
    suspend fun getLatest(fileId: Long): VersionEntity?

    @Query("SELECT MAX(versionNumber) FROM versions WHERE fileId=:fileId")
    suspend fun getHighestVersion(fileId: Long): Int?
}