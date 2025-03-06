package com.tbox.jotter.ScreenHome

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
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

    //Seçilen tag
    var selectedTag by remember { mutableStateOf<String?>(null) }


    //Sıralama düzenini kontrol eder
    var isDescending by remember { mutableStateOf(true) }

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

    // Etiket tıklama fonksiyonu, notları seçilen etikete göre filtreler
    fun onTagClick(tag: String) {
        selectedTag = tag
        filteredNotes = if (selectedTag != null) {
            notes.filter { it["tag"] == selectedTag }
        } else {
            notes
        }

    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple Note", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineLarge)  },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),

            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("chat_screen")
                },
                containerColor = MaterialTheme.colorScheme.primary
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
                            NoteCard(note, navController, onTagClick = ::onTagClick, onEdit = {}, onDelete = {})
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
    onTagClick: (String) -> Unit,
    onEdit: () ->Unit,
    onDelete: () -> Unit) {


    val titleText = note["title"]?.let {
        if (it.length > 30) it.take(30) + "..." else it
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



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val noteId = note["noteId"] ?: return@clickable

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
                        color = Color.Black
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = previewContent,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        // Italic kaldırıldı; sadece hafif transparan onSurface rengi kullanılıyor.
                        color = Color.Black.copy(alpha = 0.7f)
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
                            onClick = { onTagClick(tag) }
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
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.Black
                )
            }
            // Dropdown menü: üç nokta ikonuna tıklandığında seçenekler açılır
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        expanded = false
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        onDelete()
                    }
                )
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
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Küçük renk göstergesi (daire)
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = tag,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

