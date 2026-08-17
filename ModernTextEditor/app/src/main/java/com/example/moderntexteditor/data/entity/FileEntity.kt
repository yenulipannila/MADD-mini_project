package com.example.moderntexteditor.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fileName: String,

    val filePath: String,

    val encoding: String = "UTF-8",

    val isReadOnly: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()

)