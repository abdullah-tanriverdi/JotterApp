package com.tbox.jotter.profile


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.tbox.jotter.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    var firstName by remember { mutableStateOf("Name") }
    var lastName by remember { mutableStateOf("Surname") }
    var phoneNumber by remember { mutableStateOf("05462640611") }
    var email by remember { mutableStateOf("example@example.com") }
    var bio by remember { mutableStateOf("Bio") }

    val firestore = Firebase.firestore
    var showMenu by remember { mutableStateOf(false) }
    var profileImage by remember { mutableStateOf(R.drawable.jotter_unbackground) }

    var isEditing by remember { mutableStateOf(false) }


    fun saveUserData(uid: String, firstName: String, lastName: String, email: String, phoneNumber: String, bio: String){
        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "bio" to bio,
            "email" to email
        )


        firestore.collection("users")
            .document(uid)
            .collection("profile")
            .document("profile_data")
            .set(user)
            .addOnSuccessListener {  }
            .addOnFailureListener{ e->
                print("error: $e")
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    Button(onClick = {
                        if (isEditing){
                            val uid = Firebase.auth.currentUser?.uid ?: return@Button
                            saveUserData(
                                uid = uid,
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                phoneNumber = phoneNumber,
                                bio = bio
                            )
                        }
                        isEditing = !isEditing
                    }) {
                        if (isEditing) {
                            Icon(Icons.Filled.Save, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Icon(Icons.Filled.Edit, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomAppBar {

                //İkonların seçili olup olmadığına göre renk belirleme
                val homeIconTint = if (currentRoute == "home") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                val settingIconTint = if (currentRoute == "setting") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                val profileIconTint = if (currentRoute == "profile") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                val graphIconTint = if (currentRoute == "graph") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

                //Profil Butonu
                IconButton(onClick = { navController.navigate("profile") }, modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = profileIconTint)
                        Text(text = "Profile", style = MaterialTheme.typography.bodySmall, color = profileIconTint)
                    }
                }

                //Home Butonu
                IconButton(onClick = { navController.navigate("home") }, modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = homeIconTint)
                        Text(text = "Home", style = MaterialTheme.typography.bodySmall, color = homeIconTint)
                    }
                }



                IconButton(onClick = { navController.navigate("setting") }, modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Checklist, contentDescription = "To-Do", tint = settingIconTint)
                        Text(text = "To-Do", style = MaterialTheme.typography.bodySmall, color = settingIconTint)
                    }
                }


                //Graph Butonu
                IconButton(onClick = { navController.navigate("graph") }, modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.GridGoldenratio, contentDescription = "Graph", tint = graphIconTint)
                        Text(text = "Graph", style = MaterialTheme.typography.bodySmall, color = graphIconTint)
                    }
                }

                //Settings Butonu
                IconButton(onClick = { navController.navigate("setting") }, modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Settings, contentDescription = "Setting", tint = settingIconTint)
                        Text(text = "Settings", style = MaterialTheme.typography.bodySmall, color = settingIconTint)
                    }
                }

            }
        },

        content = { paddingValues ->


            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                Spacer(modifier = Modifier.height(40.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {


                    Image(
                        painter = painterResource(id = R.drawable.jotter_unbackground), // Placeholder image
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { showMenu = !showMenu/* Handle profile photo click */ }
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.align(Alignment.Center),
                    ) {
                        DropdownMenuItem(
                            text = { Text("Fotoğraf Yükle") },
                            onClick = {

                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Fotoğrafı Sil") },
                            onClick = {

                            }
                        )
                    }

                }

                Spacer(modifier = Modifier.height(10.dp))


                // First Name (Editable based on isEditing)
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { if (isEditing) firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Last Name (Editable based on isEditing)
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { if (isEditing) lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Number (Editable based on isEditing)
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (isEditing) phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Email (Editable based on isEditing)
                OutlinedTextField(
                    value = email,
                    onValueChange = { if (isEditing) email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bio (Editable based on isEditing)
                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (isEditing) bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing
                )
            }
        }
    )
}









