package com.tbox.jotter.ScreenHome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun Color.lighten(factor: Float): Color {
    return Color(
        red = min(1f, red + factor),
        green = min(1f, green + factor),
        blue = min(1f, blue + factor),
        alpha = alpha
    )
}

fun Color.darken(factor: Float): Color {
    return Color(
        red = max(0f, red - factor),
        green = max(0f, green - factor),
        blue = max(0f, blue - factor),
        alpha = alpha
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSimpleNote(navController: NavController, uid: String) {

    //Firestoreden alınan notlar
    var notes by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    //Filtrelenmiş notlar
    var filteredNotes by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }


    //Sıralama düzenini kontrol eder
    var isDescending by remember { mutableStateOf(true) }
    var menuExpanded by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }


    //Loading durumu
    var isLoading by remember { mutableStateOf(true) } // Yükleme durumunu takip eden state

    // 'uid' değiştiğinde asenkron olarak veri çekerken LaunchedEffect kullanılır
    LaunchedEffect(uid) {
        fetchNotesFromFirestore(
            uid = uid,
            onNotesFetched = { fetchedNotes ->
                notes = fetchedNotes
                    .filter { it["timestamp" ] != null }
                    .sortedByDescending { it["timestamp"]!!.toString() } as List<Map<String, String>>
                filteredNotes = notes
                isLoading = false
            },
            onFailure = { exception ->
                println("Error fetching notes: ${exception.message}")
                isLoading = false
            }
        )
    }


    fun updateFilteredNotes(notes: List<Map<String, String>>, query: String, descending: Boolean) {
        filteredNotes = notes.filter { note ->
            note["title"]?.contains(query, ignoreCase = true) == true ||
                    note["tag"]?.contains(query, ignoreCase = true) == true
        }.sortedByDescending { if (descending) it["timestamp"]?.toString() else null }
            .sortedBy { if (!descending) it["timestamp"]?.toString() else null }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple Note", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineLarge)  },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {


                    IconButton(onClick = { isSearchOpen = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Ara", tint = MaterialTheme.colorScheme.onPrimary)
                    }

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sırala", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("En Yeni") },
                                onClick = {
                                    isDescending = true
                                    updateFilteredNotes(notes, searchQuery, true)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("En Eski") },
                                onClick = {
                                    isDescending = false
                                    updateFilteredNotes(notes, searchQuery, false)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("chat_screen")
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Assistant, contentDescription = "Asistan", tint = Color.White)
            }
        }
    ) {
        paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            }else{

                if (filteredNotes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Notes Yet", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn {
                        items(filteredNotes) { note ->
                            NoteCard(
                                note = note,
                                navController = navController,
                                uid = uid,  // Kullanıcı kimliği gerekiyor
                                onDeleteSuccess = { noteId ->
                                    filteredNotes = filteredNotes.filter { it["noteId"] != noteId }
                                }
                            )
                        }
                    }
                }
            }

        }
        if (isSearchOpen) {
            AnimatedVisibility(
                visible = isSearchOpen,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)) // Arkaplanı koyu yapar
                        .clickable { isSearchOpen = false }, // Dışına tıklayınca kapanır
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .shadow(8.dp, shape = RoundedCornerShape(16.dp)), // Gölgelendirme ve yuvarlatılmış köşeler
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Ara",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { query ->
                                        searchQuery = query
                                        filteredNotes = if (query.isEmpty()) {
                                            notes
                                        } else {
                                            notes.filter { note ->
                                                note["title"]?.contains(query, ignoreCase = true) == true ||
                                                        note["tag"]?.contains(query, ignoreCase = true) == true // **SADECE TITLE VE TAG ARANIYOR**
                                            }
                                        }
                                    },
                                    placeholder = { Text("Başlık veya Etiket ile Ara...") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent,
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        isSearchOpen = false
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}






@Composable
fun NoteCard(
    note: Map<String, String>,
    navController: NavController,
    uid: String,
    onDeleteSuccess: (String) -> Unit) {


    val titleText = note["title"]?.take(28)?.let {
        if (it.length == 28) "$it..." else it
    } ?: "Untitled"

    // İçerik metni için karakter limiti
    val previewContent = note["content"]?.let {
        if (it.length > 50) it.take(50) + "..." else it
    } ?: "No content available"


    // Temel renk: MaterialTheme'den primary rengi alınıyor.
    val baseColor = MaterialTheme.colorScheme.tertiary
    // Gradient için aynı rengin açık ve koyu tonlarını oluşturuyoruz.
    val gradientColors = listOf(
        baseColor.lighten(0.1f),
        baseColor.darken(0.1f)
    )


    var expanded by remember { mutableStateOf(false) }

    val noteId = note["noteId"] ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {


                navController.navigate("noteDetail/$noteId")

            },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(brush = androidx.compose.ui.graphics.Brush.linearGradient(gradientColors))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Başlık


                Text(


                    text = titleText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 0.5.sp,
                         color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = previewContent,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        // Italic kaldırıldı; sadece hafif transparan onSurface rengi kullanılıyor.
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                // Tag ve tarih etiketi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    note["tag"]?.takeIf { it.isNotEmpty() }?.let { tag ->
                        TagLabel(
                            tag = tag,
                            onClick = {  }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = note["timestamp"] ?: "No Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.Black
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(0.dp, 4.dp) // Menü'nün tam üç noktanın altına gelmesini sağlar
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            navController.navigate("noteDetail/$noteId")

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            deleteNoteFromFirestore(
                                uid = uid,
                                noteId = noteId,
                                onSuccess = { onDeleteSuccess(noteId) },  // UI'den kaldır
                                onFailure = { exception -> println("Error: ${exception.message}") }
                            )
                        }
                    )
                }
            }
        }
    }
}



fun fetchNotesFromFirestore(
    uid: String,
    onNotesFetched: (List<Map<String, Any>>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users")
        .document(uid)
        .collection("notes")
        .document("simple_notes")
        .collection("user_notes")
        .whereEqualTo("type", "simple")
        .get()
        .addOnSuccessListener { documents ->
            val notes = documents.map { document ->
                document.data + ("noteId" to document.id)
            }
            onNotesFetched(notes)
        }
        .addOnFailureListener {
            onFailure(it)
        }
}






@Composable
fun TagLabel(
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
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
    }
}


fun deleteNoteFromFirestore(
    uid: String,
    noteId: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users")
        .document(uid)
        .collection("notes")
        .document("simple_notes")
        .collection("user_notes")
        .document(noteId)
        .delete()
        .addOnSuccessListener {
            println("Note deleted successfully.")
            onSuccess()
        }
        .addOnFailureListener { exception ->
            println("Error deleting note: ${exception.message}")
            onFailure(exception)
        }
}



