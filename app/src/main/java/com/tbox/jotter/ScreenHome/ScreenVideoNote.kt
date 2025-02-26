package com.tbox.jotter.ScreenHome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenVideoNote(navController: NavController) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var videoFilePath by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    val storageRef = FirebaseStorage.getInstance().reference.child("users/$userId/video_notes")
    val firestoreRef = FirebaseFirestore.getInstance()
        .collection("users")
        .document(userId)
        .collection("notes")
        .document("video_notes")
        .collection("user_notes")

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showDialog = true
        } else {
            Log.e("Camera", "Video kaydı başarısız veya iptal edildi.")
        }
    }

    fun recordVideo() {
        val videoFile = createVideoFile(context)
        videoFilePath = videoFile.absolutePath
        videoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", videoFile)

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            videoLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("Camera", "Kamera başlatılırken hata: ${e.message}")
        }
    }

    fun uploadVideo() {
        val fileUri = videoUri ?: return
        val fileName = "${System.currentTimeMillis()}.mp4"
        val fileRef = storageRef.child(fileName)

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    val videoData = hashMapOf(
                        "url" to downloadUrl.toString(),
                        "title" to title,
                        "tags" to tags,
                        "timestamp" to timestamp
                    )
                    firestoreRef.add(videoData)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Video başarıyla kaydedildi")
                            showDialog = false
                            navController.popBackStack("home", false)
                        }
                        .addOnFailureListener { e -> Log.e("Firebase", "Firestore kaydı başarısız: ${e.message}") }
                }
            }
            .addOnFailureListener { e -> Log.e("Firebase", "Video yükleme başarısız: ${e.message}") }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Video Notları") }) }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { recordVideo() },
                modifier = Modifier.size(80.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Blue)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = "Video Kaydet", tint = Color.White)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Video Bilgileri") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Etiketler") })
                }
            },
            confirmButton = {
                TextButton(onClick = { uploadVideo() }) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("İptal") }
            }
        )
    }
}

fun createVideoFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
    return File.createTempFile("VID_${timeStamp}_", ".mp4", storageDir)
}