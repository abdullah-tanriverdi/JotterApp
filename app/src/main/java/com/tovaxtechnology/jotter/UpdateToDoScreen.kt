package com.tovaxtechnology.jotter


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tovaxtechnology.jotter.HomeScreen.Todo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTodoScreen(todoId: String, navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var importance by remember { mutableStateOf("Normal") }

    var isSaving by remember { mutableStateOf(false) }

    // Load existing todo
    LaunchedEffect(todoId) {
        if (currentUser != null) {
            db.collection("todos")
                .document(currentUser.uid)
                .collection("items")
                .document(todoId)
                .get()
                .addOnSuccessListener { document ->
                    val todo = document.toObject(Todo::class.java)
                    todo?.let {
                        title = it.title
                        content = it.content
                        tag = it.tag
                        importance = it.importance
                    }
                }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jotter",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentUser == null) {
                        Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
                        return@FloatingActionButton
                    }
                    isSaving = true

                    val updatedData = mapOf(
                        "title" to title,
                        "content" to content,
                        "tag" to tag,
                        "importance" to importance,
                        "timestamp" to Calendar.getInstance().time
                    )

                    db.collection("todos")
                        .document(currentUser.uid)
                        .collection("items")
                        .document(todoId)
                        .update(updatedData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Todo updated!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                },
                containerColor = Color(0xFF4CAF50),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = "Update", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val maxTitleLength = 30

            Column {
                TextField(
                    value = title,
                    onValueChange = {
                        if (it.length <= maxTitleLength) title = it
                    },
                    label = { Text("Title") },
                    placeholder = { Text("Enter a short and clear title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                )

                Text(
                    text = "${title.length} / $maxTitleLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            val maxContentLength = 200

            Column {
                TextField(
                    value = content,
                    onValueChange = {
                        if (it.length <= maxContentLength) content = it
                    },
                    label = { Text("Content") },
                    placeholder = { Text("Describe your task in detail...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.medium),
                    maxLines = 5
                )

                // Kalan karakter sayısı
                Text(
                    text = "${content.length} / $maxContentLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }


            val maxTagLength = 20

            Column {
                TextField(
                    value = tag,
                    onValueChange = {
                        if (it.length <= maxTagLength) tag = it
                    },
                    label = { Text("Tag") },
                    placeholder = { Text("e.g. Work, Personal") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                )

                Text(
                    text = "${tag.length} / $maxTagLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }


            ImportanceSection(importance = importance, onImportanceChanged = { importance = it })        }
    }
}

