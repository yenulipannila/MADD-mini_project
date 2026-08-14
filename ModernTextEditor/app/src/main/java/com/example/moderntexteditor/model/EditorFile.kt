package com.example.moderntexteditor.model

data class EditorFile(

    val id: Long = 0,

    val fileName: String,

    val filePath: String,

    val content: String = "",

    val encoding: String = "UTF-8",

    val isReadOnly: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()

)