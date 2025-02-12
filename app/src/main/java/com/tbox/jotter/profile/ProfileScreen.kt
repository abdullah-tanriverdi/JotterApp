package com.tbox.jotter.profile


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController) {


    //Mevcut bottom bar rota bilgisini alır
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    //Durum değişkenleri
    var showOptionsProfile by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }


    //Firebase Firestore ve Storage referansları
    val firestore: FirebaseFirestore = Firebase.firestore
    val storage: FirebaseStorage = Firebase.storage
    val auth: FirebaseAuth = Firebase.auth

    //Kullanıcı ID'sini erişim
    val userId = auth.currentUser?.uid

    //Kullanıcı bilgilerini tutan değişken
    var name: String? by remember { mutableStateOf<String?>("") }
    var phoneNumber: String? by remember { mutableStateOf<String?>("") }
    var email: String? by remember { mutableStateOf<String?>("") }
    var bio: String? by remember { mutableStateOf<String?>("") }
    var birthDate : String? by remember { mutableStateOf<String?>("") }
    var profileImageUrl: String? by remember { mutableStateOf<String?>(null) }

    var daysSinceBirth by remember { mutableStateOf(0) }

    val defaultProfileImage ="https://github.com/abdullah-tanriverdi/JotterApp/raw/master/app/src/main/res/drawable/jotter_unbackground.png"


    //Kullanıcı verilerini Firestore'dan çeken method
    suspend fun getUserData(uid: String) {
        isLoading = true
        try {
            val document = firestore.collection("users")
                .document(uid)
                .collection("profile")
                .document("profile_data")
                .get()
                .await()

            profileImageUrl = document.getString("profileImageUrl") ?: defaultProfileImage


            if (document.exists()) {
                name = document.getString("name") ?: ""
                phoneNumber = document.getString("phoneNumber") ?: ""
                email = document.getString("email") ?: ""
                bio = document.getString("bio") ?: ""
                birthDate = document.getString("birthDate")
                profileImageUrl = document.getString("profileImageUrl")
            }
        } catch (e: Exception) {
            println("Error: $e")
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(userId) {
        userId?.let { uid ->
            getUserData(uid)
        }
    }


    //Profil fotoğrafını yükleyen method
    fun uploadProfileImage(uri: Uri) {
        userId?.let { uid ->
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        profileImageUrl = downloadUri.toString()
                        firestore.collection("users")
                            .document(uid)
                            .collection("profile")
                            .document("profile_data")
                            .update("profileImageUrl", downloadUri.toString())
                            .addOnSuccessListener {

                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error: $e")
                }
        }
    }


    //ActivityResultLauncher'ı oluşturma. Kullanıcıya seçici pencersini açmak için kullanılır.
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadProfileImage(it)
        }
    }





    //Profil fotoğrafını sil
    fun deleteProfileImage() {
        userId?.let { uid ->
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.delete()
                .addOnSuccessListener {
                    profileImageUrl = defaultProfileImage
                    firestore.collection("users")
                        .document(uid)
                        .collection("profile")
                        .document("profile_data")
                        .update("profileImageUrl", defaultProfileImage)
                }
                .addOnFailureListener { e ->
                    println("Error: $e")
                }
        }
    }

    LaunchedEffect(userId) {
        userId?.let { uid ->
            val document = firestore.collection("users")
                .document(uid)
                .collection("profile")
                .document("profile_data")
                .get()
                .await()
            isLoading = false
        }
    }


    fun calculateDaysSinceBirth(birthDate: String?): Int {
        try {
            if (birthDate.isNullOrEmpty()) {
                return 0
            }

            // Doğum tarihini formatla LocalDate formatına dönüştür
            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd.MM.yyy")
            } else {
                TODO("VERSION.SDK_INT < O")
            } // Eğer tarih formatınız farklıysa bunu değiştirebilirsiniz
            val birthLocalDate = LocalDate.parse(birthDate, formatter)

            // Bugünün tarihini al
            val currentDate = LocalDate.now()

            // Doğum tarihi ile bugünün arasındaki farkı hesapla
            return ChronoUnit.DAYS.between(birthLocalDate, currentDate).toInt()
        }catch (e: Exception){
            return 0
        }

    }

    LaunchedEffect(birthDate) {
        daysSinceBirth = calculateDaysSinceBirth(birthDate)
    }


    Scaffold(
        content = { paddingValues ->

            if (isLoading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Spacer(modifier = Modifier.height(10.dp))

                    //Profil kutusu
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (showOptionsProfile) {
                                            showOptionsProfile = false
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        // Profil fotoğrafı kutusu
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape) // Çerçeve ekledik
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            showOptionsProfile = true
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                painter = rememberImagePainter(
                                    data = profileImageUrl,
                                    builder = {
                                        crossfade(true) // Yumuşak geçiş efekti
                                    }
                                ),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop, // Fotoğrafı kırparak tam sığdırır
                                modifier = Modifier
                                    .size(150.dp) // Fotoğraf boyutu kutu ile aynı
                                    .clip(CircleShape) // Yuvarlak şekil
                            )

                        }

                        //Seçenekler kısmı
                        if (showOptionsProfile) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))

                            ) {
                                // Fotoğraf Yükle
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showOptionsProfile = false
                                            imagePickerLauncher.launch("image/*")
                                        }
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Upload,
                                            contentDescription = "Upload Icon",
                                            tint = MaterialTheme.colorScheme.primary
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Upload Photo",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }


                                // Çizgi
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)


                                // Fotoğraf Sil
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showOptionsProfile = false
                                            deleteProfileImage()
                                        }
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteSweep,
                                            contentDescription = "Delete Icon",
                                            tint = MaterialTheme.colorScheme.error
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Delete Photo",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(14.dp))

                    //İsim alanı
                    name?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Yaş alanı
                    Text(
                        text = "$daysSinceBirth days of living, learning, and growing!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(35.dp))


                    // Kart Başlığı
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Personal Info Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Personal Info",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }


                    //Email
                    email?.let { InfoRow(label = "Email ->", value = it) }

                    Spacer(modifier = Modifier.height(12.dp))

                    //Telefon numarası
                    phoneNumber?.let { InfoRow(label = "Phone Number ->", value = it) }

                    Spacer(modifier = Modifier.height(12.dp))

                    //Doğum tarihi
                    birthDate?.let { InfoRow(label = "Date of Birth ->", value = it) }

                    Spacer(modifier = Modifier.height(24.dp))


                    //Çizgi
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )


                    //Kart başlığı
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "To Myself Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "To Myself",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }


                    //Bio alanı
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                        shape = MaterialTheme.shapes.medium,

                        ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            bio?.let {
                                Text(
                                    text = it,

                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

        },

        //ProfileScreenEdit'e giden FAB butonu
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("profile_screen_edit")
                } ,
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                    Icon(Icons.Default.ManageAccounts , contentDescription = "Manage Accounts")


            }
        } ,bottomBar = {
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



                //To-Do Butonu
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
        }
    )

}



//Personal info kart tasarımı
@Composable
fun InfoRow(label : String, value : String ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Etiket
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Değer
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
