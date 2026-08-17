package com.example.moderntexteditor.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "versions",
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = ["id"],
            childColumns = ["fileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VersionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fileId: Long,

    val versionNumber: Int,

    val versionName: String,

    //First version stores full content
    val baseContent: String = "",

    //Later versions store only the diff/patch
    val patch: String = "",

    val contentHash: String,

    val createdAt: Long = System.currentTimeMillis()

)