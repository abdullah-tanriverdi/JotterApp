package com.tbox.jotter.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tbox.jotter.firestore.addNoteToFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController, uid: String) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // Kaydırılabilir içerik için ScrollState
    val scrollState = rememberScrollState()

    var tag by remember { mutableStateOf("") }
    var isTagVisible by remember{ mutableStateOf(false) }

    // Scaffold kullanarak yapı kuruyoruz
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Note", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {

                    IconButton(onClick = {
                    isTagVisible = !isTagVisible
                    /* Tag icon click action */ }) {

                            Icon(Icons.Filled.Tag, contentDescription = "Tag Icon", tint =  MaterialTheme.colorScheme.onPrimary)


                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // App bar'ın arka plan rengini primary yapıyoruz
                )
            )

        },
        content = { paddingValues ->
            // Scrollable Column kullanarak kaydırılabilir içerik
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)  // Ekranı kaydırılabilir hale getiriyoruz
            ) {
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
                if (isTagVisible) {
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { newTag ->
                            // Eğer boşluk bırakıldıysa ve kelime henüz başında '#' işareti yoksa, ekleyin
                            val trimmedTag = newTag.trim()
                            if (trimmedTag.isNotEmpty() && !trimmedTag.startsWith("#") && newTag.endsWith(" ")) {
                                tag = "#$trimmedTag"
                                // Yeni harf girişini engelle
                                // Burada yeni harf girişi için bir mantık eklemek gerekli
                                return@OutlinedTextField // Giriş kapatılıyor
                            } else {
                                tag = newTag
                            }
                        },
                        label = { Text("Tag") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        enabled = !tag.contains(" ")  // Boşluk bırakıldıysa yeni giriş engelleniyor
                    )
                }

                Button(
                    onClick = {

                        val tagToSave = if (tag.isNotEmpty() && !tag.startsWith("#")) {
                            "#$tag" // Tag'in başına # ekliyoruz
                        } else {
                            tag // Zaten başında # varsa olduğu gibi bırakıyoruz
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
    )
}
