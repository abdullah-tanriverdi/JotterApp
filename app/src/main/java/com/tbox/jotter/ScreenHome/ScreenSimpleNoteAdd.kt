package com.tbox.jotter.ScreenHome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSimpleNoteAdd(navController: NavController, uid: String) {

    //Not bilgilerini tutan değişken
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }

    // Kaydırılabilir içerik için ScrollState
    val scrollState = rememberScrollState()

    //Save edilme durumunu kontrol eden state değişkeni
    var isSaving by remember { mutableStateOf(false) }



    Scaffold(
        //TopBar
        topBar = {
            TopAppBar(
                title = { Text("Add New Note", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )

        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {

                // Not Başlığı için TextField
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )

                )

                // Not İçeriği için TextField
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    minLines = 4,
                    maxLines = 6,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )

                )


                // Tag input field always visible
                OutlinedTextField(
                    value = tag,
                    onValueChange = { newTag ->
                        val trimmedTag = newTag.trim()
                        if (newTag.contains(" ")) {
                            return@OutlinedTextField
                        } else {
                            tag = newTag
                        }
                    },
                    label = { Text("Tag") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val trimmedTag = tag.trim()
                            if (trimmedTag.isNotEmpty() && !trimmedTag.startsWith("#")) {
                                tag = "#$trimmedTag"
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )



                //Save Butonu
                Button(
                    onClick = {
                        isSaving = true
                        val tagToSave = if (tag.isNotEmpty() && !tag.startsWith("#")) {
                            "#$tag"
                        } else {
                            tag
                        }
                        val timestamp = System.currentTimeMillis()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDate = dateFormat.format(Date(timestamp))
                        addNoteToFirestore(
                            uid = uid,
                            title = title,
                            content = content,
                            type = "simple",
                            tag = tagToSave,
                            timestamp = formattedDate,
                            onSuccess = {
                                isSaving = false
                                navController.popBackStack("home", false)
                            },
                            onFailure = { exception ->
                                isSaving = false
                                println("Error adding note: ${exception.message}")
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text("Save Note")
                    }
                }
            }
        }
    )
}


fun addNoteToFirestore(
    uid: String,
    title: String,
    content: String,
    type: String,
    tag: String?,
    timestamp: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
){

    val firestore = FirebaseFirestore.getInstance()
    val noteData = hashMapOf(
        "title" to title,
        "content" to content,
        "timestamp" to System.currentTimeMillis(),
        "type" to type,
        "timestamp" to timestamp

    )
    tag?.let {
        noteData["tag"] = it
    }


    firestore.collection("users")
        .document(uid)
        .collection("notes")
        .document("simples_notes")
        .collection("user_notes")
        .add(noteData)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener{ exception ->
            onFailure(exception)

        }

}
