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
import com.google.firebase.firestore.Query


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
    var isLoading by remember { mutableStateOf(true) }

    val filteredTodos = todos
        .filter { it.title.contains(searchQuery, ignoreCase = true) }
        .sortedBy { it.completed } // completed == false olanlar üstte, true olanlar altta


    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            db.collection("todos")
                .document(currentUser.uid)
                .collection("items")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    todos = result.documents.map { doc ->
                        val todo = doc.toObject(Todo::class.java) ?: Todo()
                        todo.copy(id = doc.id)
                    }
                    isLoading = false
                }
                .addOnFailureListener{
                    isLoading = false
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
                actions = {
                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Clear Completed Tasks"
                        )
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Clear Completed Tasks") },
                            text = { Text("Are you sure you want to delete all completed tasks?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDialog = false
                                        if (currentUser != null) {
                                            val completedTodos = todos.filter { it.completed }
                                            completedTodos.forEach { todo ->
                                                db.collection("todos")
                                                    .document(currentUser.uid)
                                                    .collection("items")
                                                    .document(todo.id)
                                                    .delete()
                                            }
                                            todos = todos.filterNot { it.completed }
                                        }
                                    }
                                ) {
                                    Text("Yes", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
,
                        colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },



    bottomBar = {
        val selectedColor = Color(0xFF2196F3)
        val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        Box {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        if (selectedTab != 0) {
                            selectedTab = 0
                            navController.navigate("home")  // Home ekranına git
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.HomeWork,
                            modifier = Modifier.size(28.dp),
                            contentDescription = "Home",
                            tint = if (selectedTab == 0) selectedColor else unselectedColor
                        )
                    },
                    label = {
                        Text(
                            "Home",
                            color = if (selectedTab == 0) selectedColor else unselectedColor
                        )
                    }
                )

                Spacer(modifier = Modifier.width(48.dp)) // Ortadaki buton için boşluk

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        // Profile ekranındaysak, sadece navigasyonu tetikle
                        if (selectedTab != 1) {
                            selectedTab = 1
                            // Profile ekranında değilsen navigasyona git
                            if (navController.currentBackStackEntry?.destination?.route != "profile") {
                                navController.navigate("profile")
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PersonPin,
                            modifier = Modifier.size(28.dp),
                            contentDescription = "Profile",
                            tint = if (selectedTab == 1) selectedColor else unselectedColor
                        )
                    },
                    label = {
                        Text(
                            "Profile",
                            color = if (selectedTab == 1) selectedColor else unselectedColor
                        )
                    }
                )
            }

            // Floating Action Button efekti
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("addToDo") },
                    containerColor = Color(0xFF4CAF50),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        modifier = Modifier.size(40.dp),
                        contentDescription = "Add Todo"

                    )
                }
            }
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



            if (isLoading){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }

            }else{
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
                                            .addOnSuccessListener {
                                                todos = todos.filter {
                                                    it.id != todo.id
                                                }
                                            }
                                    }
                                },

                                onUpdate = {
                                    navController.navigate("updateToDo/${todo.id}")
                                }
                            )
                        }
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
    onDelete: () -> Unit ,
    onUpdate: () -> Unit
) {

    val backgroundColor = when {
        todo.completed -> Color(0xFFE0E0E0) // Tamamlanmışsa gri (örneğin #E0E0E0)
        todo.importance == "Important" -> Color(0xFFFFCDD2) // açık kırmızı
        todo.importance == "Postpone" -> Color(0xFFC8E6C9)  // açık yeşil
        todo.importance == "Normal" -> Color(0xFFBBDEFB)    // açık mavi
        else -> MaterialTheme.colorScheme.surfaceVariant
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

                var expanded by remember { mutableStateOf(false) }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Update") },
                            onClick = {
                                expanded = false
                                onUpdate() // update işlemine yönlendirme
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Label, // Burada istediğiniz ikonu kullanabilirsiniz
                            contentDescription = "Tag Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp) // İkon boyutunu buradan ayarlayabilirsiniz
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // İkon ile etiket arasına boşluk ekleyin
                        Text(
                            text = todo.tag,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }




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

