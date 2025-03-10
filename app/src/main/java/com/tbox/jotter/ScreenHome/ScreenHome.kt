package com.tbox.jotter.ScreenHome


import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tbox.jotter.Auth.AuthViewModel
import com.tbox.jotter.ScreenQuickNotes.darken
import com.tbox.jotter.ScreenQuickNotes.lighten


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenHome(navController: NavController) {




    //Geçerli rota bilgisi saklama (bottom bar)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Kullanıcı adı için cache (önbellek)
    var userName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var lastUpdated by remember { mutableStateOf(System.currentTimeMillis()) }

    var showDialog by remember { mutableStateOf(false) }
    val authViewModel = AuthViewModel()

    // **Firestore'dan kullanıcı adını çekme**
    LaunchedEffect(userId, lastUpdated) {
        userId?.let { uid ->
            if (userName.isNullOrEmpty()) { // Eğer cache'de varsa tekrar çekme
                firestore.collection("users")
                    .document(uid)
                    .collection("profile")
                    .document("profile_data")
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val newName = document.getString("name") ?: ""
                            if (newName != userName) {
                                userName = newName
                            }
                        }
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading = false
                    }
            } else {
                isLoading = false
            }
        }
    }






    Scaffold(
        //BottomBar
      bottomBar = {
          BottomAppBar {

              //İkonların seçili olup olmadığına göre renk belirleme
              val homeIconTint = if (currentRoute == "home") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
              val settingIconTint = if (currentRoute == "setting") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
              val profileIconTint = if (currentRoute == "profile") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
              val graphIconTint = if (currentRoute == "graph") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

              //Profil Butonu
              IconButton(onClick = { navController.navigate("profile") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = profileIconTint)
                      Text(text = "Profile", style = MaterialTheme.typography.bodySmall, color = profileIconTint)
                  }
              }

              // Home Butonu
              IconButton(
                  onClick = {
                      if (currentRoute != "home") navController.navigate("home")
                  },
                  modifier = Modifier.weight(1f, true)
              ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Default.Home, contentDescription = "Home", tint = homeIconTint)
                      Text(text = "Home", style = MaterialTheme.typography.bodySmall, color = homeIconTint)
                  }
              }



          }
      },
        //TopBar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Jotter Home",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp, // 🔹 Çıkış butonu ikonu
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )

            // 📌 Çıkış Onay Diyaloğu
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Sign Out") },
                    text = { Text("Are you sure you want to exit the app?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                authViewModel.signout() //  Çıkış işlemi
                                navController.navigate("login") {
                                    popUpTo("0") { inclusive = true } //  Home geçmişini temizle
                                }
                            }
                        ) {
                            Text("Yes", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("No", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        },


        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ** Kullanıcı Karşılama Mesajı**
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = when {
                                isLoading -> "Yükleniyor..."
                                !userName.isNullOrEmpty() -> "Welcome, $userName "
                                else -> "Welcome!"
                            },
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "What is recorded, not what is remembered, stays in mind.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Temel renk: MaterialTheme'den primary rengi alınıyor.
                val baseColor = MaterialTheme.colorScheme.tertiary
                // Gradient için aynı rengin açık ve koyu tonlarını oluşturuyoruz.
                val gradientColors = listOf(
                    baseColor.lighten(0.1f),
                    baseColor.darken(0.1f)
                )

                // 🔵 **Ana Menü Kartları (Quick Actions)**
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 📌 **Yeni Not Ekle Kartı**
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable { navController.navigate("quick_notes_add") },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground ),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = androidx.compose.ui.graphics.Brush.linearGradient(gradientColors))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EditNote,
                                contentDescription = "Not Ekle",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Add New Note",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Quickly create a new note.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    //  **Tüm Notlarım Kartı**
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable { navController.navigate("quick_notes") },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground ),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = androidx.compose.ui.graphics.Brush.linearGradient(gradientColors))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = "Notlarım",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "View All Your Notes",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Quickly review and edit your notes.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    //  **Sohbet Asistanı Kartı**
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable { navController.navigate("quick_notes_chatbot") },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground ),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = androidx.compose.ui.graphics.Brush.linearGradient(gradientColors))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = "Jotter Chatbot",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Jotter Assistant",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Try the assistant that analyzes your notes and helps you!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

    )
}







