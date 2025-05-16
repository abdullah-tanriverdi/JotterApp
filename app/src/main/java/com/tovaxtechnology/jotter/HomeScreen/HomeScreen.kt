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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.Query


import com.google.firebase.Timestamp
import com.tovaxtechnology.jotter.R
import com.tovaxtechnology.jotter.ui.theme.Quicksand
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
        .sortedBy { it.completed }


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
                        text = stringResource( id = R.string.app_name ),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Quicksand,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.FolderDelete,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = stringResource(id= R.string.clear_task)
                        )
                    }

                    if (showDialog) {
                        AlertDialog(
                            containerColor = MaterialTheme.colorScheme.surface,
                            onDismissRequest = { showDialog = false },
                            title = { Text(
                                text = stringResource(id = R.string.clear_task),
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Quicksand,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ) },
                            text = {  Text(
                                text = stringResource(id = R.string.clear_task_sure),
                                fontWeight = FontWeight.Normal,
                                fontFamily = Quicksand,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ) },
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
                                    Text(stringResource(id = R.string.yes),fontWeight = FontWeight.Normal,
                                        fontFamily = Quicksand,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.error)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text(stringResource(id = R.string.cancel),
                                        fontFamily = Quicksand,
                                        fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        )
                    }
                }
,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },



    bottomBar = {
        val selectedColor = MaterialTheme.colorScheme.tertiary
        val unselectedColor = MaterialTheme.colorScheme.onTertiary
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
                            navController.navigate("home") {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.HomeWork,
                            modifier = Modifier.size(28.dp),
                            contentDescription = stringResource(id=R.string.home),
                            tint = if (selectedTab == 0) selectedColor else unselectedColor
                        )
                    },
                    label = {
                        Text(
                            stringResource(id=R.string.home),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Quicksand,
                            color = if (selectedTab == 0) selectedColor else unselectedColor
                        )
                    }
                )

                Spacer(modifier = Modifier.width(48.dp))

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {

                        if (selectedTab != 1) {
                            selectedTab = 1
                            if (navController.currentBackStackEntry?.destination?.route != "profile") {
                                navController.navigate("profile") {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PersonPin,
                            modifier = Modifier.size(28.dp),
                            contentDescription =stringResource(id= R.string.profile),
                            tint = if (selectedTab == 1) selectedColor else unselectedColor
                        )
                    },
                    label = {
                        Text(
                            stringResource(id= R.string.profile),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Quicksand,
                            color = if (selectedTab == 1) selectedColor else unselectedColor
                        )
                    }
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("addToDo") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        modifier = Modifier.size(40.dp),
                        contentDescription = stringResource(id=R.string.addToDo)

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


            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_task),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Quicksand,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                },
                        leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ContentPasteSearch,
                        contentDescription = stringResource(id=R.string.search),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_todos),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Quicksand,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 30.sp

                        )
                    }
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

    val importanceColor = when {
        todo.completed -> MaterialTheme.colorScheme.surfaceVariant
        todo.importance == "Important" -> MaterialTheme.colorScheme.tertiary
        todo.importance == "Postpone" -> MaterialTheme.colorScheme.primary
        todo.importance == "Normal" -> MaterialTheme.colorScheme.onBackground
        else -> MaterialTheme.colorScheme.surfaceVariant
    }


    val cardColor = if (todo.completed) {
        MaterialTheme.colorScheme.onTertiary
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
        Column {
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
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Quicksand,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                var expanded by remember { mutableStateOf(false) }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = stringResource(id = R.string.more_options),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(id = R.string.update),
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Quicksand,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            onClick = {
                                expanded = false
                                onUpdate()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(id = R.string.delete),
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Quicksand,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
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
                    fontWeight = FontWeight.Medium,
                    fontFamily = Quicksand,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface,
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
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = stringResource(id = R.string.tag_icon),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = todo.tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(importanceColor)
            )
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

