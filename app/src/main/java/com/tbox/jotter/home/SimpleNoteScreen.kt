package com.tbox.jotter.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tbox.jotter.firestore.fetchNotesFromFirestore
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleNoteScreen(navController: NavController, uid: String) {
    var notes by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var filteredNotes by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var selectedTag by remember { mutableStateOf<String?>(null) }

    var showSortMenu by remember { mutableStateOf(false) }
    var isDescending by remember { mutableStateOf(true) }

    // Firestore'dan basit notları çekiyoruz
    LaunchedEffect(uid) {
        fetchNotesFromFirestore(
            uid = uid,
            onNotesFetched = { fetchedNotes ->
                notes = fetchedNotes
                    .filter { it["timestamp" ] != null }
                    .sortedByDescending { it["timestamp"]!!.toString() } as List<Map<String, String>>
                filteredNotes = notes
            },
            onFailure = { exception ->
                println("Error fetching notes: ${exception.message}")
            }
        )
    }


    fun onTagClick(tag: String) {
        selectedTag = tag
        filteredNotes = if (selectedTag != null) {
            notes.filter { it["tag"] == selectedTag } // Seçilen tag'e sahip olanları filtrele
        } else {
            notes // Tüm notları göster
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Simple Notes", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {


                        Box{
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("En Eski") },
                                    onClick = {
                                        isDescending = false
                                        notes = notes.sortedBy { it["timestamp"]!!.toString() } // Eskiden Yeniye
                                        showSortMenu = false
                                        filteredNotes = notes
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("En Yeni") },
                                    onClick = {
                                        isDescending = true
                                        notes = notes.sortedByDescending { it["timestamp"]!!.toString() } // Yeniden Eskiye
                                        showSortMenu = false
                                        filteredNotes = notes
                                    }
                                )
                            }
                        }
                    },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )

        }
    ) {
        paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {

            // Eğer bir tag seçilmişse, bunu göster ve temizleme butonu ekle
            selectedTag?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filtre: $it", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.weight(1f))
                    AssistChip(
                        label = { Text("Filtreyi Temizle") },
                        onClick = { selectedTag = null
                                  filteredNotes = notes},
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.error)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (filteredNotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Notes Yet", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn {
                    items(filteredNotes) { note ->
                        NoteCard(note, navController, onTagClick = ::onTagClick)
                    }
                }
            }
        }
    }
}


@Composable
fun NoteCard(note: Map<String, String>, navController: NavController, onTagClick: (String) -> Unit) {
    val previewContent = note["content"]?.take(20)?.plus("...") ?: "No content available"



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val noteId = note["noteId"]?: return@clickable

                navController.navigate("noteDetail/$noteId")

            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Başlık
            Text(
                text = note["title"] ?: "Untitled",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            // İçeriğin ilk birkaç kelimesi
            Text(
                text = previewContent,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Tag ve zaman etiketi
            Row(verticalAlignment = Alignment.CenterVertically) {
                note["tag"]?.takeIf { it.isNotEmpty() }?.let { tag ->
                    AssistChip(
                        label = { Text(tag) },
                        onClick = {  onTagClick(tag)
                        // tag tıklama
                             },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondary)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = note["timestamp"] ?: "No Date", style = MaterialTheme.typography.labelSmall)
            }
        }



    }





}

