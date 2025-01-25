package com.tbox.jotter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SimpleNoteScreen(navController: NavController, uid: String) {
    var notes by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    // Firestore'dan basit notları çekiyoruz
    LaunchedEffect(uid) {
        fetchNotesFromFirestore(
            uid = uid,
            onNotesFetched = { fetchedNotes ->
                notes = fetchedNotes as List<Map<String, String>>
                // Veriyi loglayalım
                println("Fetched Notes: $notes")
            },
            onFailure = { exception ->
                println("Error fetching notes: ${exception.message}")
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Simple Notes", style = MaterialTheme.typography.titleLarge)

        if (notes.isEmpty()) {
            Text("No Notes Yet")
        } else {
            // Notları listeleme
            notes.forEach { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                       ,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Title: ${note["title"]}")
                        Text("Content: ${note["content"]}")
                    }
                }
            }
        }
    }
}
