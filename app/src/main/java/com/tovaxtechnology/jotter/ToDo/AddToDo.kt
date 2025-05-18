package com.tovaxtechnology.jotter.ToDo

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import com.tovaxtechnology.jotter.R
import com.tovaxtechnology.jotter.ui.theme.Quicksand
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var importance by remember { mutableStateOf("Normal") }
    var isSaving by remember { mutableStateOf(false) }




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

                    val todo = hashMapOf(
                        "title" to title,
                        "content" to content,
                        "tag" to tag,
                        "importance" to importance,
                        "timestamp" to (selectedDate ?: FieldValue.serverTimestamp())
                    )

                    db.collection("todos")
                        .document(currentUser.uid)
                        .collection("items")
                        .add(todo)
                        .addOnSuccessListener {
                            isSaving = false
                            Toast.makeText(context, context.getString(R.string.added), Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            isSaving = false
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

    ) { paddingValues ->
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
@Composable
fun ImportanceSection(
    importance: String,
    onImportanceChanged: (String) -> Unit
) {


    val options = listOf(
        Triple(stringResource(id=R.string.important), MaterialTheme.colorScheme.tertiary, Icons.Default.PriorityHigh),
        Triple(stringResource(id=R.string.postpone), MaterialTheme.colorScheme.onBackground, Icons.Default.Schedule),
        Triple(stringResource(id=R.string.normal), MaterialTheme.colorScheme.primary, Icons.Default.Assignment)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { (label, color, icon) ->
            val isSelected = importance == label

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) BorderStroke(2.dp, color) else null,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onImportanceChanged(label) }
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) color else color.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontFamily = Quicksand,
                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
        }
    }
}

