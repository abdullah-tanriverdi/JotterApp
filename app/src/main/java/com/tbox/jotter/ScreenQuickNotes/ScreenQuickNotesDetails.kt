package com.tbox.jotter.ScreenQuickNotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenQuickNotesDetails(navController: NavController, uid: String, noteId: String) {
    var noteDetails by remember { mutableStateOf<Map<String, String>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var menuExpanded by remember { mutableStateOf(false) }

    // Firestore'dan not detaylarını çek
    LaunchedEffect(noteId) {
        fetchNoteDetailsFromFirestore(
            uid = uid,
            noteId = noteId,
            onSuccess = { note ->
                noteDetails = note
                isLoading = false
            },
            onFailure = { exception ->
                println("Error fetching note: ${exception.message}")
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note View", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineLarge)  },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),

                actions = {
                    // 3 Noktalı Menü Butonu
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            offset = DpOffset(x = 0.dp, y = 8.dp) // Menü'nün 3 noktanın altına inmesini sağlar
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    menuExpanded = false
                                    deleteNoteFromFirestore(
                                        uid = uid,
                                        noteId = noteId,
                                        onSuccess = {
                                            navController.popBackStack() // Not silindikten sonra geri dön
                                        },
                                        onFailure = { exception ->
                                            println("Error deleting note: ${exception.message}")
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            )


        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("quick_notes_edit/$uid/$noteId")
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Note")
            }
        }


    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (noteDetails != null) {
                val note = noteDetails!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()), // **Tam ekran kaydırma**
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {


                        Text(
                            text = note["title"] ?: "Untitled",
                        style = MaterialTheme.typography.titleMedium.copy(
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        )
                    Divider(
                        color = Color.Gray,
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // İçerik
                    Text(
                        text = note["content"] ?: "No content available",
                        style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Etiket (Tag)
                    note["tag"]?.let {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = "Tag",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))



                    // Güncellenme tarihini veya oluşturulma tarihini göster
                    val lastUpdated = note["updatedTimestamp"] as? String
                    val createdOn = note["timestamp"] as? String
                    val displayDate = when {
                        lastUpdated != null -> Pair("Last updated on: ", lastUpdated)
                        createdOn != null -> Pair("Created on: ", createdOn)
                        else -> null
                    }

                    if (displayDate != null) {
                        Text(
                            text = "${displayDate.first} ${displayDate.second}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        Text(
                            text = "Date information not available",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }
            } else {
                Text("Note not found.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}




// Firestore'dan belirli bir notun detaylarını çekme fonksiyonu
fun fetchNoteDetailsFromFirestore(
    uid: String,
    noteId: String,
    onSuccess: (Map<String, String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users")
        .document(uid)
        .collection("notes")
        .document("quick_notes")
        .collection("user_notes")
        .document(noteId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                onSuccess(document.data as Map<String, String>)
            } else {
                onFailure(Exception("Note not found"))
            }
        }
        .addOnFailureListener { onFailure(it) }
}
