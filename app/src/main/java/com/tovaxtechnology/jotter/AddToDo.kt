package com.tovaxtechnology.jotter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavController

@Composable
fun AddToDo(navController: NavController) {
    // State for title and description
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    val db = Firebase.firestore  // Firebase Firestore bağlantısı

    // Task kaydetme fonksiyonu
    fun addTask() {
        if (title.text.isNotBlank() && description.text.isNotBlank()) {
            val taskData = hashMapOf(
                "title" to title.text,
                "description" to description.text,
                "created_at" to System.currentTimeMillis()
            )

            // Firestore'a veri ekleme
            db.collection("tasks")
                .add(taskData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        navController.context,
                        "Task added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack() // Ekranı geri al
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        navController.context,
                        "Error adding task: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                navController.context,
                "Title and Description cannot be empty.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // UI Bileşenleri
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Add a New Task", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Title TextField
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description TextField
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = { addTask() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Save Task")
        }
    }
}
