package com.tbox.jotter.ScreenHome

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenVoiceNoteList(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var audioFiles by remember { mutableStateOf<List<VoiceNote>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var playingFile by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var fileToDelete by remember { mutableStateOf<VoiceNote?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val firestoreRef = FirebaseFirestore.getInstance()
        .collection("users").document(userId)
        .collection("notes").document("voice_notes")
        .collection("user_notes")

    fun fetchVoiceNotes() {
        isLoading = true
        firestoreRef.get().addOnSuccessListener { querySnapshot ->
            audioFiles = querySnapshot.documents.mapNotNull { doc ->
                val url = doc.getString("url")
                val title = doc.getString("title")
                val tags = doc.getString("tags")
                val timestamp = doc.getString("timestamp")
                val id = doc.id
                if (url != null && timestamp != null) VoiceNote(id, url, title, tags, timestamp) else null
            }.sortedByDescending { it.timestamp }
            isLoading = false
        }.addOnFailureListener {
            Log.e("Firebase", "Ses dosyaları alınırken hata: ${it.message}")
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { fetchVoiceNotes() }

    fun toggleAudio(fileId: String, fileUrl: String) {
        if (playingFile == fileId) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            playingFile = null
        } else {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(fileUrl)
                prepareAsync()
                setOnPreparedListener { start(); playingFile = fileId }
                setOnCompletionListener { playingFile = null; release(); mediaPlayer = null }
            }
        }
    }

    fun deleteAudio() {
        val file = fileToDelete ?: return
        val storageFileRef = FirebaseStorage.getInstance().reference.child("users/$userId/voice_notes/${file.id}")
        showDeleteDialog = false

        firestoreRef.document(file.id).delete().addOnSuccessListener {
            storageFileRef.delete().addOnSuccessListener {
                fetchVoiceNotes()
                fileToDelete = null
                navController.popBackStack("home", false)
            }.addOnFailureListener { Log.e("Firebase", "Storage silme başarısız: ${it.message}") }
        }.addOnFailureListener {
            Log.e("Firestore", "Firestore silme başarısız: ${it.message}")
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Ses Kayıtları") }) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (audioFiles.isEmpty()) {
                Text("Henüz kayıtlı bir ses yok.")
            } else {
                LazyColumn {
                    items(audioFiles) { voiceNote ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = voiceNote.title ?: "Başlıksız", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = voiceNote.tags?.let { "Etiketler: $it" } ?: "Etiket yok", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = formatDate(voiceNote.timestamp), style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { toggleAudio(voiceNote.id, voiceNote.url) }) {
                                        Icon(imageVector = if (playingFile == voiceNote.id) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = "Oynat/Durdur")
                                    }
                                    IconButton(onClick = { fileToDelete = voiceNote; showDeleteDialog = true }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Ses Kaydını Sil") },
            text = { Text("Bu ses kaydını silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(onClick = { deleteAudio()
                    navController.popBackStack("home", false)}) {
                    Text("Evet, Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

fun formatDate(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        date?.let { outputFormat.format(it) } ?: "Bilinmeyen Tarih"
    } catch (e: Exception) {
        "Bilinmeyen Tarih"
    }
}

data class VoiceNote(val id: String, val url: String, val title: String?, val tags: String?, val timestamp: String)
