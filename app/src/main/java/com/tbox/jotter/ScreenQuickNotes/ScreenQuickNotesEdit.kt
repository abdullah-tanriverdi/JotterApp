package com.tbox.jotter.ScreenQuickNotes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenQuickNotesEdit(navController: NavController, uid: String, noteId: String) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("No Tag") }
    var isTagError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isTagDropdownExpanded by remember { mutableStateOf(false) }
    var isTitleError by remember { mutableStateOf(false) }
    var isContentError by remember { mutableStateOf(false) }

    val availableTags = listOf("Personal", "Work", "Ideas", "Important", "Shoping","Fitness", "Travel","Custom")

    // Firestore'dan not detaylarını çek
    LaunchedEffect(noteId) {
        fetchNoteDetailsFromFirestore(
            uid = uid,
            noteId = noteId,
            onSuccess = { note ->
                title = note["title"] ?: ""
                content = note["content"] ?: ""
                tag = note["tag"] ?: "No Tag"
                isLoading = false
            },
            onFailure = { exception ->
                println("Error fetching note: ${exception.message}")
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Note Edit", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineLarge)  },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {

                        val isTitleValid = title.isNotBlank()
                        val isTagValid= tag.isNotBlank() && !tag.contains(" ") && tag.length <=10
                        var isContentValid = content.isNotBlank()

                        isTitleError = !isTitleValid
                        isTagError =!isTagValid
                        isContentError =!isContentValid

                        if (isTitleValid && isTagValid && isContentValid){
                            updateNoteInFirestore(uid, noteId, title, content, tag) {
                                navController.popBackStack()
                        }

                        }
                    }) {
                        Icon(Icons.Default.ChecklistRtl, contentDescription = "Save", tint = Color.White)
                    }
                },

            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                if (it.isNotBlank()) {
                                    isTitleError = false // Kullanıcı yazdıkça hatayı kaldır
                                }
                            },
                            label = { Text("Title") },
                            singleLine = true,
                            isError = isTitleError, // Hata durumunu yönet
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = if (isTitleError) Color.Red else MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (isTitleError) Color.Red else Color.Gray,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Eğer hata varsa, hemen altında hata mesajını göster
                        if (isTitleError) {
                            Text(
                                text = "Title cannot be empty!",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

// **Tag Seçme Alanı**
                    Column {

                        Text(text = "Tag", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                        Spacer(modifier = Modifier.height(6.dp))



                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                    .clickable { isTagDropdownExpanded = true }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Label,
                                    contentDescription = "Tag",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon( // Açılır menünün varlığını belli etmek için aşağı ok simgesi
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }


                        DropdownMenu(
                            expanded = isTagDropdownExpanded,
                            onDismissRequest = { isTagDropdownExpanded = false },
                            offset = DpOffset(x= 0.dp , y =4.dp)
                        ) {
                            availableTags.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        tag = if (option == "Custom") "" else option
                                        isTagDropdownExpanded = false
                                        isTagError = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Eğer "Custom" seçildiyse manuel tag girme alanı
                        if (tag.isEmpty() || availableTags.contains(tag).not()) {
                            OutlinedTextField(
                                value = tag,
                                onValueChange = {
                                    if (it.length <= 10 && !it.contains(" ")) {  // 15 karakter sınırı ve tek kelime kontrolü
                                        tag = it
                                        isTagError = false
                                    }
                                },
                                label = { Text("Custom Tag") },
                                isError = isTagError,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = if (isTagError) Color.Red else MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = if (isTagError) Color.Red else Color.Gray,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            if (isTagError) {
                                Text(
                                    text = "Tag must be one word and max 10 characters!",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                    }

                    // İçerik düzenleme alanı
                    Column(modifier = Modifier.fillMaxWidth()) {
                        BasicTextField(
                            value = content,
                            onValueChange = {
                                content = it
                                if (it.isNotBlank()) {
                                    isContentError = false // Kullanıcı yazarsa hata kaybolsun
                                }
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )

                        // Eğer içerik boşsa placeholder gibi bir görünüm ekle
                        if (content.isBlank()) {
                            Text(
                                text = "Enter your content...",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }

                        // Eğer hata varsa, hemen altında hata mesajını göster
                        if (isContentError) {
                            Text(
                                text = "Content cannot be empty!",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }



                    Spacer(modifier = Modifier.height(12.dp))

                }
            }
        }
    }
}

// Firestore'daki notu güncelleme fonksiyonu
fun updateNoteInFirestore(uid: String, noteId: String, title: String, content: String, tag: String, onComplete: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    val timestamp = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(timestamp))

    val updatedNote = mapOf(
        "title" to title,
        "content" to content,
        "tag" to tag,
        "updatedTimestamp" to formattedDate,
        "timestamp" to formattedDate
    )

    firestore.collection("users")
        .document(uid)
        .collection("notes")
        .document("quick_notes")
        .collection("user_notes")
        .document(noteId)
        .update(updatedNote)
        .addOnSuccessListener {
            println("Note updated successfully")
            onComplete()
        }
        .addOnFailureListener { exception ->
            println("Error updating note: ${exception.message}")
        }
}
