package com.tbox.jotter.ScreenHome

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenVideoList(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var videoList by remember { mutableStateOf<List<VideoData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Listener referansını saklamak için değişken
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    DisposableEffect(Unit) {
        if (userId == null) {
            errorMessage = "Kullanıcı kimliği bulunamadı."
            isLoading = false
            return@DisposableEffect onDispose {}
        }

        val listener = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("notes")
            .document("video_notes")
            .collection("user_notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Veri çekme hatası: ${error.message}")
                    errorMessage = "Veri çekme hatası: ${error.message}"
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    videoList = snapshot.documents.mapNotNull { it.toObject(VideoData::class.java) }
                    isLoading = false
                    errorMessage = null
                } else {
                    Log.d("Firestore", "Hiç video bulunamadı.")
                    videoList = emptyList()
                    errorMessage = "Henüz eklenmiş video notu yok."
                    isLoading = false
                }
            }

        listenerRegistration = listener

        // Component kaldırıldığında listener'ı temizle
        onDispose {
            listenerRegistration?.remove()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Video Notları") }) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                !errorMessage.isNullOrEmpty() -> Text(
                    text = errorMessage ?: "Bilinmeyen hata!",
                    color = MaterialTheme.colorScheme.error
                )
                else -> LazyColumn {
                    items(videoList) { video -> VideoCard(video) }
                }
            }
        }
    }
}
@Composable
fun VideoCard(video: VideoData) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(video.url), "video/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = video.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Etiketler: ${video.tags}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Tarih: ${video.timestamp}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

data class VideoData(
    val url: String = "",
    val title: String = "",
    val tags: String = "",
    val timestamp: String = ""
)
