package com.tbox.jotter.ScreenProfile


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.tbox.jotter.ScreenQuickNotes.darken
import com.tbox.jotter.ScreenQuickNotes.lighten
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenProfile(navController: NavController) {


    //Mevcut bottom bar rota bilgisini alır
    val currentRoute : String? = navController.currentBackStackEntryAsState().value?.destination?.route

    //Profil fotoğrafı seçeneklerini tutan state değişkeni
    var showOptionsProfile: Boolean by remember { mutableStateOf(false) }

    //Verilerin yüklenmesi durumunu tutan state değişkeni
    var isLoading : Boolean by remember { mutableStateOf(true) }


    //Firebase Firestore ve Storage referansları
    val firestore: FirebaseFirestore = Firebase.firestore
    val storage: FirebaseStorage = Firebase.storage
    val auth: FirebaseAuth = Firebase.auth

    //Kullanıcı ID'sini erişim
    val userId :String? = auth.currentUser?.uid

    //Kullanıcı bilgilerini tutan state değişkenleri
    var name: String? by remember { mutableStateOf<String?>("") }
    var phoneNumber: String? by remember { mutableStateOf<String?>("") }
    var email: String? by remember { mutableStateOf<String?>("") }
    var bio: String? by remember { mutableStateOf<String?>("") }
    var birthDate : String? by remember { mutableStateOf<String?>("") }
    var profileImageUrl: String? by remember { mutableStateOf<String?>(null) }

    //Gün sayısını tutan state değişkeni
    var daysSinceBirth : Int by remember { mutableStateOf(0) }

    //Varsayılan profil resmi URL'si
    val defaultProfileImage ="https://github.com/abdullah-tanriverdi/JotterApp/raw/master/app/src/main/res/drawable/jotter_unbackground.png"




    //Kullanıcı verilerini Firestore'dan çeken method
    suspend fun getUserData(uid: String) {
        isLoading = true
        try {
            val document  = firestore.collection("users")
                .document(uid)
                .collection( "profile")
                .document("profile_data")
                .get()
                .await()

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

    //Kullanıcı verilerini yükler
    LaunchedEffect(userId) {
        userId?.let { uid ->
            getUserData(uid)
            isLoading = false
        }
    }


    //Profil fotoğrafını yükleyen method
    fun uploadProfileImage(uri: Uri) {
        userId?.let { uid ->
            val storageRef = storage.reference.child("users/$uid/profile/profile_image.jpg")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        profileImageUrl = downloadUri.toString()
                        firestore.collection("users")
                            .document(uid)
                            .collection("profile")
                            .document("profile_data")
                            .update("profileImageUrl", downloadUri.toString())
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
        uri?.let { uri ->
            uploadProfileImage(uri)
        }
    }



    //Profil fotoğrafını sil
    fun deleteProfileImage() {
        userId?.let { uid ->
            val storageRef = storage.reference.child("users/$uid/profile/profile_image.jpg")
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




    //Gün sayısını hesaplayan method
    fun calculateDaysSinceBirth(birthDate: String?): Int {
        try {
            if (birthDate.isNullOrEmpty()) {
                return 0
            }

            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd.MM.yyy")
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val birthLocalDate = LocalDate.parse(birthDate, formatter)

            val currentDate = LocalDate.now()

            return ChronoUnit.DAYS.between(birthLocalDate, currentDate).toInt()
        }catch (e: Exception){
            return 0
        }

    }

    //Doğum tarihi değiştiğinde gün sayısını yeniden hesaplar
    LaunchedEffect(birthDate) {
        daysSinceBirth = calculateDaysSinceBirth(birthDate)
    }


    Scaffold(
        content = { paddingValues ->

            //Loading kontrollü
            if (isLoading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    //Yükleniyor göstergesi
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

                    // Temel renk: MaterialTheme'den primary rengi alınıyor.
                    val baseColor = MaterialTheme.colorScheme.tertiary
                    // Gradient için aynı rengin açık ve koyu tonlarını oluşturuyoruz.
                    val gradientColors = listOf(
                        baseColor.lighten(0.1f),
                        baseColor.darken(0.1f)
                    )

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
                                .border(6.dp, brush = androidx.compose.ui.graphics.Brush.linearGradient(gradientColors), CircleShape)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            showOptionsProfile = true
                                        },
                                        onTap = {
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
                                        crossfade(true)
                                    }
                                ),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
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
                        // İsim alanı
                        Text(
                            text = name.takeIf { !it.isNullOrEmpty() } ?: "Kullanıcı",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Gün alanı
                    // Gün alanı
                    AnnotatedString.Builder().apply {
                        append("You've experienced ")
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                        append("$daysSinceBirth")
                        pop()
                        append(" days of adventure, growth, and endless possibilities! 🎉")
                    }.let { text ->
                        Text(
                            text = text.toAnnotatedString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }


                    Spacer(modifier = Modifier.height(35.dp))


                    AppleStyleCardHeader(title = "Personal Info", icon = Icons.Default.Info)







                    //Email
                    email?.let {  AppleStyleInfoRow(label = "Email", value = it) }

                    Spacer(modifier = Modifier.height(12.dp))

                    //Telefon numarası
                    phoneNumber?.let { AppleStyleInfoRow(label = "Phone Number", value = it) }

                    Spacer(modifier = Modifier.height(12.dp))

                    //Doğum tarihi
                    birthDate?.let {  AppleStyleInfoRow(label = "Date of Birth", value = it) }



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
                    AppleStyleCardHeader(title = "To Myself", icon = Icons.Default.Stars)


                   bio.let {
                       AppleStyleBio(bio = it)

                   }


                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

        },
        //TopBar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },

        //ProfileScreenEdit'e giden FAB butonu
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("profile_edit")
                } ,
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
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
                IconButton(onClick = {   if (currentRoute != "profile") {
                    navController.navigate("profile")
                } },
                    modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = profileIconTint)
                        Text(text = "Profile", style = MaterialTheme.typography.bodySmall, color = profileIconTint)
                    }
                }

                //Home Butonu
                IconButton(onClick = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true } // Tüm stack'i temizler
                    }
                }, modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = homeIconTint)
                        Text(text = "Home", style = MaterialTheme.typography.bodySmall, color = homeIconTint)
                    }
                }




            }
        }
    )

}@Composable
fun AppleStyleCardHeader(title: String, icon: ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // İkon (Apple tarzı, büyük ve minimalist)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .padding(end = 8.dp)
            )

            // Başlık (Minimalist ve güçlü tipografi)
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Altındaki İnce Çizgi (Apple’ın iOS tasarımına uygun)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f) // Çizginin uzunluğu
                .height(2.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 4.dp)
        )
    }
}



@Composable
fun AppleStyleBio(bio: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        bio?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Apple tarzı ince çizgi (soft ayraç)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        )
    }
}

@Composable
fun AppleStyleInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Etiket (Minimal, sol tarafa hizalanmış)
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Değer (Apple tarzında sade ve net)
            Text(
                text = value,
                modifier = Modifier.weight(3f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            )
        }

        // Apple Stili İnce Ayırıcı Çizgi
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        )
    }
}
