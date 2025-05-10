package com.tovaxtechnology.jotter.HomeScreen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import androidx.compose.foundation.lazy.items

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var selectedTab by remember { mutableStateOf(0) }
    var todos by remember { mutableStateOf<List<Todo>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTodos = todos.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    // Firestore'dan verileri al
    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            db.collection("todos")
                .document(currentUser.uid)
                .collection("items")
                .get()
                .addOnSuccessListener { result ->
                    todos = result.documents.map { doc ->
                        val todo = doc.toObject(Todo::class.java) ?: Todo()
                        todo.copy(id = doc.id)  // Firestore'dan gelen veriye ID ekleme
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
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text("Profile")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("addToDo") },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Todo",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    label = {
                        Text("")
                    },
                    alwaysShowLabel = false
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = {
                        Text("Settings")
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search your tasks...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Todo List Display
            if (todos.isEmpty()) {
                Text("No Todos", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTodos) { todo ->
                        TodoItemCard(
                            todo = todo,
                            onCheckedChange = { isChecked ->
                                if (currentUser != null) {
                                    db.collection("todos")
                                        .document(currentUser.uid)
                                        .collection("items")
                                        .document(todo.id)
                                        .update("completed", isChecked)

                                    todos = todos.map {
                                        if (it.id == todo.id) it.copy(completed = isChecked) else it
                                    }
                                }
                            },
                            onDelete = {
                                if (currentUser != null) {
                                    db.collection("todos")
                                        .document(currentUser.uid)
                                        .collection("items")
                                        .document(todo.id)
                                        .delete()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: Todo,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = if (todo.completed) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = todo.completed,
                        onCheckedChange = onCheckedChange
                    )
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (todo.content.isNotEmpty()) {
                Text(
                    text = todo.content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (todo.tag.isNotEmpty()) {
                    AssistChip(
                        onClick = {},
                        label = { Text(todo.tag) },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                AssistChip(
                    onClick = {},
                    label = { Text(todo.importance) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = when (todo.importance) {
                            "Important" -> Color.Red
                            "Postpone" -> Color.Gray
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                )

                if (todo.timestamp != null) {
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            .format(todo.timestamp.toDate()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}




data class Todo(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val tag: String = "",
    val importance: String = "Normal",
    val completed: Boolean = false,
    val timestamp: Timestamp? = null
)

