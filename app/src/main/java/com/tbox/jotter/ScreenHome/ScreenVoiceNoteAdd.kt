package com.tbox.jotter.ScreenHome

import android.Manifest
import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenVoiceNoteAdd(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var isRecording by remember { mutableStateOf(false) }
    var recordedFilePath by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    var recorder: MediaRecorder? by remember { mutableStateOf(null) }

    val user = auth.currentUser
    val userId = user?.uid ?: return

    fun startRecording() {
        val fileName = "${context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/recorded_audio.3gp"
        recordedFilePath = fileName
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
                start()
                isRecording = true
            } catch (e: IOException) {
                Log.e("Recorder", "Recording failed: ${e.message}")
            }
        }
    }
    fun saveToFirestore(fileUrl: String) {
        val noteData = hashMapOf(
            "url" to fileUrl,
            "title" to title,
            "tags" to tags,
            "timestamp" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .document("voice_notes")
            .collection("user_notes")
            .add(noteData)
            .addOnSuccessListener { Log.d("Firestore", "Note saved successfully") }
            .addOnFailureListener { Log.e("Firestore", "Failed to save note") }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        showDialog = true // Durdurunca dialog aç
    }
    fun uploadToFirebase() {
        recordedFilePath?.let { filePath ->
            val file = Uri.fromFile(File(filePath))
            val storageRef = storage.reference.child("users/$userId/voice_notes/${file.lastPathSegment}")

            storageRef.putFile(file)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { Log.e("Firebase", "Upload failed: ${it.message}") }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Voice Note",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(if (isRecording) Color.Red else Color.Blue, shape = CircleShape)
                        .clickable {
                            if (isRecording) stopRecording() else startRecording()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Record",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Kaydı Kaydet") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("Başlık") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = tags,
                                    onValueChange = { tags = it },
                                    label = { Text("Etiketler (virgülle ayırın)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                uploadToFirebase()
                                showDialog = false
                                navController.popBackStack("home", false)
                            }) {
                                Text("Kaydet")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false
                                navController.popBackStack("home", false)}) {
                                Text("İptal")
                            }
                        }
                    )
                }
            }
        }
    )
}
