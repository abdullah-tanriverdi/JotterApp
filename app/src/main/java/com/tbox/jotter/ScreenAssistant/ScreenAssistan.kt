package com.tbox.jotter.ScreenAssistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel) {
    var userMessage by remember { mutableStateOf("") }
    val chatHistory by viewModel.chatHistory.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchNotes()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(chatHistory) { message ->
                Text(text = message, modifier = Modifier.padding(8.dp))
            }
        }

        Row {
            TextField(
                value = userMessage,
                onValueChange = { userMessage = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                viewModel.sendMessage(userMessage)
                userMessage = ""
            }) {
                Text("Gönder")
            }
        }
    }
}
