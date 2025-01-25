package com.tbox.jotter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddNoteScreen(navController: NavController, uid: String) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Başlık
        Text(
            text = "Add New Note",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Not Başlığı için TextField
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Note Title") },

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Not İçeriği için TextField
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Note Content") },

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Kaydet Butonu
        Button(
            onClick = {
                addNoteToFirestore(
                    uid = uid,
                    title = title,
                    content = content,
                    type = "simple",
                    onSuccess = {
                        // Note başarıyla Firestore'a kaydedildiğinde ana ekrana dön
                        navController.popBackStack("home", false)
                    },
                    onFailure = { exception ->
                        // Hata durumunda bir hata mesajı yazdır
                        println("Error adding note: ${exception.message}")
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Save Note")
        }

    }
}
