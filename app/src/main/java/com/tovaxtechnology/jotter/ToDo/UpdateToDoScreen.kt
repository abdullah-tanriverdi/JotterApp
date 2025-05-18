package com.tovaxtechnology.jotter.ToDo


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tovaxtechnology.jotter.HomeScreen.Todo
import com.tovaxtechnology.jotter.R
import com.tovaxtechnology.jotter.ui.theme.Quicksand
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTodoScreen(todoId: String, navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var importance by remember { mutableStateOf("Normal") }

    var isSaving by remember { mutableStateOf(false) }

    // Load existing todo
    LaunchedEffect(todoId) {
        if (currentUser != null) {
            db.collection("todos")
                .document(currentUser.uid)
                .collection("items")
                .document(todoId)
                .get()
                .addOnSuccessListener { document ->
                    val todo = document.toObject(Todo::class.java)
                    todo?.let {
                        title = it.title
                        content = it.content
                        tag = it.tag
                        importance = it.importance
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentUser == null || isSaving) return@FloatingActionButton
                    isSaving = true

                    val updatedData = mapOf(
                        "title" to title,
                        "content" to content,
                        "tag" to tag,
                        "importance" to importance,
                        "timestamp" to Calendar.getInstance().time
                    )

                    db.collection("todos")
                        .document(currentUser.uid)
                        .collection("items")
                        .document(todoId)
                        .update(updatedData)
                        .addOnSuccessListener {
                            Toast.makeText(context, context.getString(R.string.added), Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, " $context.getString(R.string.error) ${":"} ${it.message}", Toast.LENGTH_LONG).show()
                        }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .alpha(if (isSaving) 0.5f else 1f)
                    .defaultMinSize(minWidth = 120.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = stringResource(R.string.save),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource( id = R.string.save),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Quicksand,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) {  paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val maxTitleLength = 30

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        if (it.length <= maxTitleLength) title = it
                    },
                    label = {
                        Text(text = stringResource(R.string.title) , fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,)
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.title_placeholder),  fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${title.length} / $maxTitleLength",
                    fontWeight = FontWeight.Normal,
                    fontFamily = Quicksand,
                    color = if (title.length >= maxTitleLength)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 4.dp)
                )
            }


            val maxContentLength = 200

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        if (it.length <= maxContentLength) content = it
                    },
                    label = { Text(
                        stringResource(id=R.string.content), fontWeight = FontWeight.Normal,
                        fontFamily = Quicksand,) },
                    placeholder = { Text(
                        stringResource(id=R.string.describe),
                        fontWeight = FontWeight.Normal,
                        fontFamily = Quicksand,) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(2.dp))


                Text(
                    text = "${content.length} / $maxContentLength",
                    fontWeight = FontWeight.Normal,
                    fontFamily = Quicksand,
                    color = if (content.length >= maxContentLength)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 4.dp)
                )
            }


            val maxTagLength = 20

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = tag,
                    onValueChange = {
                        if (it.length <= maxTagLength) tag = it
                    },
                    label = { Text(
                        stringResource(id=R.string.tag) , fontWeight = FontWeight.Normal,
                        fontFamily = Quicksand,) },
                    placeholder = { Text(
                        stringResource(id=R.string.eg) , fontWeight = FontWeight.Normal,
                        fontFamily = Quicksand,) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                )

                Text(
                    text = "${tag.length} / $maxTagLength",
                    fontWeight = FontWeight.Normal,
                    fontFamily = Quicksand,
                    color = if (tag.length >= maxTagLength)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 4.dp)
                )
            }



            ImportanceSection(importance = importance, onImportanceChanged = { importance = it })        }
    }
}