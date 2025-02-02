package com.tbox.jotter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tbox.jotter.firestore.fetchNoteDetailFromFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(noteId: String, uid: String, navController: NavController) {
    var noteDetails by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }  // Yükleniyor durumu ekliyoruz
    var errorMessage by remember { mutableStateOf<String?>(null) }  // Hata mesajı

    // Firestore'dan notun detaylarını çekiyoruz
    LaunchedEffect(noteId) {
        fetchNoteDetailFromFirestore(noteId = noteId, uid = uid, onNoteFetched = { fetchedNote ->
            noteDetails = fetchedNote
            isLoading = false  // Veri geldi, loading durumu bitsin
        }, onFailure = { exception ->
            errorMessage = exception.message  // Hata mesajını alıyoruz
            isLoading = false  // Yükleniyor durumu bitti
        })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Edit işlemi için navigasyon eklenebilir */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        }
    ){ paddingValues ->
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
                    ) {
                        // Başlık
                        Text(
                            text = noteDetails["title"] ?: "No title",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // İçerik
                        Text(
                            text = noteDetails["content"] ?: "No content",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tag ve Zaman Bilgisi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
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