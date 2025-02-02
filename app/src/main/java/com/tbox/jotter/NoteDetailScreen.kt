package com.tbox.jotter

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tbox.jotter.firestore.fetchNoteDetailFromFirestore
import com.tbox.jotter.firestore.updateNoteInFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(noteId: String, uid: String, navController: NavController) {
    var noteDetails by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }  // Yükleniyor durumu ekliyoruz
    var errorMessage by remember { mutableStateOf<String?>(null) }  // Hata mesajı

    var isEditing by remember { mutableStateOf(false) }
    var scrollState  = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    // Firestore'dan notun detaylarını çekiyoruz
    LaunchedEffect(noteId) {
        fetchNoteDetailFromFirestore(noteId = noteId, uid = uid, onNoteFetched = { fetchedNote ->
            noteDetails = fetchedNote

            title = fetchedNote["title"] ?: ""
            content = fetchedNote["content"] ?: ""

            isLoading = false  // Veri geldi, loading durumu bitsin
        }, onFailure = { exception ->
            errorMessage = exception.message  // Hata mesajını alıyoruz
            isLoading = false  // Yükleniyor durumu bitti
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("my NOtte", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // App bar'ın arka plan rengini primary yapıyoruz
                )
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {

                        val timestamp = System.currentTimeMillis()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDate = dateFormat.format(Date(timestamp))
                        updateNoteInFirestore(
                            uid = uid,
                            noteId = noteId,
                            title = title,
                            content = content,
                            timestamp = formattedDate,
                            onSuccess = { isEditing = false
                                noteDetails = noteDetails.toMutableMap().apply {
                                    this["timestamp"] = formattedDate
                                }},
                            onFailure = { errorMessage = it.message }
                        )
                    } else {
                        isEditing = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(if (isEditing) Icons.Filled.Check else Icons.Filled.Edit, contentDescription = "Edit or Save")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $errorMessage", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                noteDetails.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Note not found.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                                .padding(8.dp)
                        ) {
                            if (isEditing) {
                                BasicTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    textStyle = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        isEditing = false
                                    })
                                )
                            } else {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray)
                                .padding(8.dp)
                        ) {
                            if (isEditing) {
                                BasicTextField(
                                    value = content,
                                    onValueChange = { content = it },
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        isEditing = false
                                    })
                                )
                            } else {
                                Text(
                                    text = content,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),

                        ) {
                            noteDetails["tag"]?.let { tag ->
                                Text(
                                    text = "Tag: $tag",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Text(
                                text = noteDetails["timestamp"] ?: "No timestamp",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}