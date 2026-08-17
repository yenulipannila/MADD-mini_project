package com.example.moderntexteditor.file

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moderntexteditor.data.database.AppDatabase
import com.example.moderntexteditor.data.repository.FileRepository
import com.example.moderntexteditor.data.repository.VersionRepository
import com.example.moderntexteditor.editor.CodeEditor
import com.example.moderntexteditor.editor.CodeFormatter
import com.example.moderntexteditor.editor.MarkdownPreview
import com.example.moderntexteditor.manager.AutoSaveManager
import com.example.moderntexteditor.manager.CrashRecoveryManager
import com.example.moderntexteditor.manager.DiffUtilManager
import com.example.moderntexteditor.manager.FileManager
import com.example.moderntexteditor.model.EditorFile
import com.example.moderntexteditor.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TextEditor() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ------------------------------------------------------------------
    // PERSISTENCE LAYER
    // ------------------------------------------------------------------

    val database = remember {
        AppDatabase.getDatabase(context)
    }

    val fileRepository = remember {
        FileRepository(
            fileDao = database.fileDao(),
            fileManager = FileManager(context)
        )
    }

    val diffUtilManager = remember {
        DiffUtilManager()
    }

    val versionRepository = remember {
        VersionRepository(
            versionDao = database.versionDao(),
            diffUtilManager = diffUtilManager
        )
    }

    val autoSaveManager = remember {
        AutoSaveManager(context)
    }

    val crashRecoveryManager = remember {
        CrashRecoveryManager(autoSaveManager)
    }

    // FILE STATE
    var text by remember {
        mutableStateOf("")
    }

    var fileName by remember {
        mutableStateOf("Untitled.kt")
    }

    var currentFileUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var currentFile by remember {
        mutableStateOf<EditorFile?>(null)
    }

    var isReadOnly by remember {
        mutableStateOf(false)
    }

    var recentFiles by remember {
        mutableStateOf<List<EditorFile>>(emptyList())
    }

    var saveMessage by remember {
        mutableStateOf<String?>(null)
    }

    // VERSION HISTORY / CRASH RECOVERY DIALOGS
    var showVersionHistory by remember {
        mutableStateOf(false)
    }

    var showRecoveryDialog by remember {
        mutableStateOf(false)
    }

    var pendingRecoveryContent by remember {
        mutableStateOf<String?>(null)
    }

    // SEARCH / REPLACE STATE
    var searchText by remember {
        mutableStateOf("")
    }

    var replaceText by remember {
        mutableStateOf("")
    }

    // UNDO / REDO
    val undoStack = remember {
        mutableStateListOf<String>()
    }

    val redoStack = remember {
        mutableStateListOf<String>()
    }

    // EDITOR SETTINGS
    var wordWrap by remember {
        mutableStateOf(true)
    }

    var showPreview by remember {
        mutableStateOf(false)
    }

    // DETERMINE FILE TYPE
    val fileType = when {
        fileName.endsWith(".md", ignoreCase = true) -> "markdown"
        fileName.endsWith(".markdown", ignoreCase = true) -> "markdown"
        else -> "kotlin"
    }

    // ------------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------------

    suspend fun refreshRecentFiles() {
        recentFiles = fileRepository.getAllFiles()
    }

    suspend fun activateFile(editorFile: EditorFile) {
        if (text.isNotEmpty() && currentFile?.id != editorFile.id) {
            undoStack.add(text)
        }

        currentFile = editorFile
        text = editorFile.content
        fileName = editorFile.fileName
        isReadOnly = editorFile.isReadOnly
        redoStack.clear()

        val latest = versionRepository.getLatestVersion(editorFile.id)

        if (latest == null) {
            versionRepository.createVersion(
                editorFile.id,
                editorFile.content,
                "Version 1"
            )
        } else {
            val latestContent = versionRepository.getVersionContent(
                editorFile.id,
                latest.versionNumber
            )

            if (latestContent != editorFile.content) {
                versionRepository.createVersion(
                    editorFile.id,
                    editorFile.content
                )
            }
        }

        val recovery = crashRecoveryManager.checkForRecovery(
            editorFile.id,
            editorFile.content
        )

        if (recovery.hasRecovery) {
            pendingRecoveryContent = recovery.content
            showRecoveryDialog = true
        }

        refreshRecentFiles()
    }

    fun saveCurrentFile() {
        scope.launch {
            val existing = currentFile

            if (existing == null) {
                val isPlaceholderName =
                    fileName.isBlank() ||
                            fileName == "Untitled.txt" ||
                            fileName == "Untitled.kt"

                val name =
                    if (isPlaceholderName) {
                        "Untitled-${System.currentTimeMillis()}.txt"
                    } else {
                        fileName
                    }

                val created = fileRepository.createFile(
                    name,
                    text
                )

                activateFile(created)
                saveMessage = "Saved as Version 1"
                return@launch
            }

            if (existing.isReadOnly) {
                saveMessage = "File is read-only — disable read-only to save."
                return@launch
            }

            fileRepository.saveFile(
                existing.id,
                text
            )

            versionRepository.createVersion(
                existing.id,
                text
            )

            autoSaveManager.deleteAutoSave(existing.id)

            currentFile = existing.copy(
                content = text,
                updatedAt = System.currentTimeMillis()
            )

            saveMessage = "Saved as a new version"
            refreshRecentFiles()
        }
    }

    LaunchedEffect(Unit) {
        refreshRecentFiles()
    }

    LaunchedEffect(currentFile?.id) {
        val fileId = currentFile?.id ?: return@LaunchedEffect

        while (isActive) {
            delay(Constants.AUTOSAVE_INTERVAL)
            autoSaveManager.autoSave(fileId, text)
        }
    }

    LaunchedEffect(saveMessage) {
        if (saveMessage != null) {
            delay(2500)
            saveMessage = null
        }
    }

    // MAIN SCREEN
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Modern Text Editor",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FILE ACTIONS TOOLBAR (TWO ROWS)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ROW 1: New, Open, Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NewButton(
                        currentText = text,
                        onTextChange = { text = it },
                        onFileNameChange = {
                            fileName = it
                            currentFile = null
                            isReadOnly = false
                        },
                        onUriChange = { currentFileUri = it },
                        undoStack = undoStack,
                        redoStack = redoStack
                    )

                    OpenButton(
                        onFileOpened = { openedText, uri, name ->
                            currentFileUri = uri
                            scope.launch {
                                val existing = fileRepository.findByName(name)
                                val editorFile = if (existing != null) {
                                    fileRepository.saveFile(existing.id, openedText)
                                    fileRepository.getFile(existing.id)
                                        ?: fileRepository.createFile(name, openedText)
                                } else {
                                    fileRepository.createFile(name, openedText)
                                }
                                activateFile(editorFile)
                            }
                        }
                    )

                    Button(
                        onClick = { saveCurrentFile() }
                    ) {
                        Text("Save")
                    }
                }

                // ROW 2: Save As, History
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SaveButton(
                        text = text,
                        fileName = fileName,
                        onFileSaved = { uri, name ->
                            currentFileUri = uri
                            fileName = name
                        }
                    )

                    OutlinedButton(
                        onClick = { showVersionHistory = true },
                        enabled = currentFile != null
                    ) {
                        Text("History")
                    }
                }
            }
        }

        if (saveMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = saveMessage.orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // COMPACT EDITOR CONTROLS
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // ROW 1: WORD WRAP & READ-ONLY TOGGLES SIDE-BY-SIDE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Word Wrap",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = wordWrap,
                            onCheckedChange = { wordWrap = it }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Read-only",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isReadOnly,
                            enabled = currentFile != null,
                            onCheckedChange = { checked ->
                                isReadOnly = checked
                                currentFile?.let { file ->
                                    scope.launch {
                                        fileRepository.setReadOnly(file.id, checked)
                                        currentFile = file.copy(isReadOnly = checked)
                                        refreshRecentFiles()
                                    }
                                }
                            }
                        )
                    }
                }

                // ROW 2: PREVIEW CHIPS & FORMAT BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = !showPreview,
                            onClick = { showPreview = false },
                            label = { Text("Editor") }
                        )

                        FilterChip(
                            selected = showPreview,
                            onClick = { showPreview = true },
                            label = { Text("Preview") }
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (!isReadOnly && fileType == "kotlin") {
                                text = CodeFormatter.formatKotlin(text)
                            }
                        }
                    ) {
                        Text("Format Code")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // INLINED COMPACT FIND & REPLACE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Find & Replace",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 13.sp
                )

                // SEARCH ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search text...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Button(
                        onClick = { /* Execute Search logic if needed */ },
                        modifier = Modifier.height(40.dp),
                        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 0.dp)
                    ) {
                        Text("Search", fontSize = 12.sp)
                    }
                }

                // REPLACE ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = replaceText,
                        onValueChange = { replaceText = it },
                        placeholder = { Text("Replace with...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Button(
                        onClick = {
                            if (searchText.isNotEmpty() && text.contains(searchText) && !isReadOnly) {
                                undoStack.add(text)
                                text = text.replace(searchText, replaceText)
                                redoStack.clear()
                            }
                        },
                        modifier = Modifier.height(40.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                    ) {
                        Text("Replace", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // UNDO / REDO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            RollbackButtons(
                text = text,
                onTextChange = { text = it },
                undoStack = undoStack,
                redoStack = redoStack
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // EDITOR TITLE
        Text(
            text = "Editor",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // CODE EDITOR / MARKDOWN PREVIEW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (showPreview) {
                MarkdownPreview(
                    markdown = text,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CodeEditor(
                    text = text,
                    onTextChange = {
                        if (it != text) {
                            undoStack.add(text)
                        }
                        text = it
                        redoStack.clear()
                    },
                    modifier = Modifier.fillMaxSize(),
                    fileType = fileType,
                    wordWrap = wordWrap,
                    readOnly = isReadOnly
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // RECENT FILES BAR
        if (recentFiles.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Recent Files",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 2.dp)
                ) {
                    items(recentFiles) { editorFile ->
                        val isSelected = currentFile?.id == editorFile.id
                        val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        val formattedDate = dateFormat.format(Date(editorFile.updatedAt))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .width(140.dp)
                                .clickable {
                                    scope.launch {
                                        activateFile(editorFile)
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = editorFile.fileName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // VERSION HISTORY DIALOG
    if (showVersionHistory && currentFile != null) {
        VersionHistoryDialog(
            fileId = currentFile!!.id,
            currentText = text,
            versionRepository = versionRepository,
            diffUtilManager = diffUtilManager,
            onRestore = { restoredContent ->
                if (text.isNotEmpty()) {
                    undoStack.add(text)
                }
                text = restoredContent
                redoStack.clear()
                showVersionHistory = false
            },
            onDismiss = { showVersionHistory = false }
        )
    }

    // CRASH RECOVERY DIALOG
    if (showRecoveryDialog) {
        CrashRecoveryDialog(
            onRestore = {
                pendingRecoveryContent?.let {
                    text = it
                }
                showRecoveryDialog = false
                pendingRecoveryContent = null
            },
            onDiscard = {
                currentFile?.let { file ->
                    scope.launch {
                        autoSaveManager.deleteAutoSave(file.id)
                    }
                }
                showRecoveryDialog = false
                pendingRecoveryContent = null
            }
        )
    }
}