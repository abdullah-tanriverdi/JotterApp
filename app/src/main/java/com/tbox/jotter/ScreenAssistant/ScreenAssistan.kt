package com.tbox.jotter.ScreenAssistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tbox.jotter.ScreenHome.darken
import com.tbox.jotter.ScreenHome.lighten

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel) {
    var userMessage by remember { mutableStateOf("") }
    val chatHistory by viewModel.chatHistory.collectAsState()
    var profileImageUrl by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        viewModel.fetchNotes()
        fetchUserProfileImage { url -> profileImageUrl = url }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jotter Chatbot", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineLarge)  },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true
            ) {
                items(chatHistory.reversed()) { message ->
                    ChatBubble(message, profileImageUrl)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    placeholder = { Text("Mesajınızı yazın...") },
                    modifier = Modifier
                        .weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )


                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = {
                        if (userMessage.isNotBlank()) {
                            viewModel.sendMessage(userMessage)
                            userMessage = ""
                        }
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: String, userProfileImageUrl: String?) {
    val isUserMessage = message.startsWith("Sen: ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isUserMessage) {
            ProfileImage(imageUrl = "https://github.com/abdullah-tanriverdi/JotterApp/raw/master/app/src/main/res/drawable/jotter_unbackground.png")
            Spacer(modifier = Modifier.width(8.dp))
        }


        // Temel renk: MaterialTheme'den primary rengi alınıyor.
        val baseColor = MaterialTheme.colorScheme.tertiary
        // Gradient için aynı rengin açık ve koyu tonlarını oluşturuyoruz.
        val gradientColors = listOf(
            baseColor.lighten(0.1f),
            baseColor.darken(0.1f)
        )



        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isUserMessage) baseColor.darken(0.1f) else baseColor.lighten(0.1f))
                .padding(12.dp)
        ) {
            Text(
                text = message.replace("Sen: ", "").replace("Asistan: ", ""),
                color = if (isUserMessage) Color.White else MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                textAlign = if (isUserMessage) TextAlign.End else TextAlign.Start
            )
        }

        if (isUserMessage) {
            Spacer(modifier = Modifier.width(8.dp))
            ProfileImage(imageUrl = userProfileImageUrl)
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?) {
    AsyncImage(
        model = imageUrl ?: "https://www.w3schools.com/howto/img_avatar.png",
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

fun fetchUserProfileImage(onImageFetched: (String?) -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users")
        .document(uid)
        .collection("profile")
        .document("profile_data")
        .get()
        .addOnSuccessListener { document ->
            val profileImageUrl = document.getString("profileImageUrl")
            onImageFetched(profileImageUrl)
        }
        .addOnFailureListener {
            onImageFetched(null)
        }
}