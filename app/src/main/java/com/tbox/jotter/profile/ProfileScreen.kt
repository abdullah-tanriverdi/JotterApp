package com.tbox.jotter.profile


import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.tbox.jotter.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController) {


    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    var name by remember { mutableStateOf("") }

    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    val firestore = Firebase.firestore
    val storage = Firebase.storage

    var showMenu by remember { mutableStateOf(false) }
val auth = Firebase.auth

    var isEditing by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }

    var profileImageUrl by remember { mutableStateOf<String?>(null) }


    val userId = auth.currentUser?.uid



    fun loadUserData(uid :String){
        isLoading = true
        firestore.collection("users")
            .document(uid)
            .collection("profile")
            .document("profile_data")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    name = document.getString("name") ?: ""

                    phoneNumber = document.getString("phoneNumber") ?: ""
                    email = document.getString("email") ?: ""
                    bio = document.getString("bio") ?: ""
                    profileImageUrl = document.getString("profileImageUrl")
                }
                isLoading = false
            }
            .addOnFailureListener{
                e->
                isLoading = false
                print("Error")
            }
    }

    fun saveUserData(uid: String){
        val user = hashMapOf(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "bio" to bio,
            "email" to email,
            "profileImageUrl" to profileImageUrl
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


    fun uploadProfileImage ( uri: Uri){

       // profileImageUrl = uri.toString()
        userId?.let{
            uid->
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener {
                        downloadUri ->
                        profileImageUrl = downloadUri.toString()
                        saveUserData(uid)
                    }
                }
        }
    }


    fun deleteProfileImage() {
        userId?.let { uid ->
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.delete()
                .addOnSuccessListener {

                    val defaultImageUrl = "https://github.com/abdullah-tanriverdi/JotterApp/raw/master/app/src/main/res/drawable/jotter_unbackground.png"
                    // Update the profileImageUrl in Firestore to null (or remove it)
                    val userRef = firestore.collection("users")
                        .document(uid)
                        .collection("profile")
                        .document("profile_data")

                    userRef.update("profileImageUrl", defaultImageUrl)
                        .addOnSuccessListener {
                            // After updating Firestore, set profileImageUrl to null in the UI state
                            profileImageUrl = defaultImageUrl
                            saveUserData(uid) // Save the updated profile data without the image URL
                        }
                        .addOnFailureListener { e ->
                            // Handle error in Firestore update
                            println("Error updating Firestore: $e")
                        }
                }
                .addOnFailureListener { e ->
                    // Handle error in Firebase Storage delete
                    println("Error deleting image: $e")
                }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
            uri: Uri? ->
        uri?.let {
            uploadProfileImage(it)
        }
    }




LaunchedEffect(profileImageUrl) {
    profileImageUrl?.let {
        userId?.let {
            uid ->
            saveUserData(uid)
        }
    }
}



    LaunchedEffect(userId) {
        userId?.let { loadUserData(it) }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Profile" else "Profile",
                       color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    Button(onClick = {
                        if (isEditing) {
                            userId?.let { saveUserData(it) }
                        }
                        isEditing = !isEditing
                    }) {
                        if (isEditing) {
                            Icon(Icons.Filled.Save, contentDescription = "Save", tint = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onPrimary)
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

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator() // Yükleniyor göstergesi
                }
            } else {
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

                        if (profileImageUrl != null) {
                            AsyncImage(
                                model = profileImageUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clickable { showMenu = !showMenu

                                    }
                                    .background(Color.Magenta, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.jotter_unbackground),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clickable { showMenu = !showMenu
                                   }
                                    .background(Color.Gray, CircleShape)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.align(Alignment.Center),
                        ) {
                            DropdownMenuItem(
                                text = { Text("Fotoğraf Yükle") },
                                onClick = {
                                    showMenu = false
                                    imagePickerLauncher.launch("image/*")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Fotoğrafı Sil") },
                                onClick = {
                                    showMenu = false
                                    deleteProfileImage()

                                }
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(10.dp))


                    // First Name (Editable based on isEditing)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (isEditing) name = it },
                        label = { Text("Name") },
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
                        onValueChange = { if (isEditing && it.length<=75)
                        {
                            bio =it
                        }},
                        label = { Text("to myself") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = !isEditing
                    )
                }
            }


        }
    )
}









