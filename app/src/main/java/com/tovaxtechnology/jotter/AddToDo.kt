package com.tovaxtechnology.jotter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var importance by remember { mutableStateOf("Normal") }

    val calendar = Calendar.getInstance()

    fun openDateTimePicker() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        calendar.set(year, month, day, hour, minute)
                        selectedDate = calendar.time
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Todo") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentUser == null) {
                        Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
                        return@FloatingActionButton
                    }

                    val todo = hashMapOf(
                        "title" to title,
                        "content" to content,
                        "tag" to tag,
                        "importance" to importance,
                        "timestamp" to (selectedDate ?: FieldValue.serverTimestamp())
                    )

                    db.collection("todos")
                        .document(currentUser.uid)
                        .collection("items")
                        .add(todo)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Todo added!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // kayıt sonrası geri dön
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("Tag") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Importance",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ImportanceCheckbox("Important", importance) { importance = it }
                ImportanceCheckbox("Normal", importance) { importance = it }
                ImportanceCheckbox("Postpone", importance) { importance = it }
            }

        }
    }
}

@Composable
fun ImportanceCheckbox(label: String, current: String, onChecked: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = current == label,
            onCheckedChange = { if (it) onChecked(label) }
        )
        Text(label)
    }
}
