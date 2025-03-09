package com.tbox.jotter.ScreenProfile


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenProfileEdit (navController: NavController, userId: String?) {

    //Firebase Firestore referansı
    val firestore: FirebaseFirestore = Firebase.firestore

    //Kullanıcı bilgilerini tutan değişken
    var name: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    var birthDate: String by remember { mutableStateOf("") }
    var phoneNumber: String by remember { mutableStateOf("") }
    var bio: String by remember { mutableStateOf("") }

    //Birth Date için hata mesajı durumu
    var birthDateError: Boolean by remember { mutableStateOf(false) }
    var birthDateErrorMessage: String? by remember { mutableStateOf<String?>(null) }

    //Email hata mesajı durumu
    var emailError: Boolean by remember { mutableStateOf(false) }
    var emailErrorMessage: String? by remember { mutableStateOf<String?>(null) }

    var nameError: Boolean by remember { mutableStateOf(false) }
    var nameErrorMessage: String? by remember { mutableStateOf(null) }

    var phoneNumberError : Boolean by remember { mutableStateOf(false) }
    var phoneNumberMessage : String? by remember { mutableStateOf(null) }

    var bioError: Boolean by remember { mutableStateOf(false) }
    var bioMessage : String ? by remember { mutableStateOf(null) }


    //Regex ile tarih formatını kontrol etme
    val datePattern = Regex("""^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.\d{4}$""")

    //Regex ile email formatını kontrol etme
    val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    //Indicator durumu(Loading) firestore'dan verileri çekme
    var isLoading: Boolean by remember { mutableStateOf(true) }

    //Indicator durumu (saving) firestore'a verileri save etme
    var isSaving: Boolean by remember { mutableStateOf(false) }


    //Kullanıcı verilerini Firestore'dan çekme
    LaunchedEffect(userId) {
        userId?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .collection("profile")
                .document("profile_data")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name") ?: ""
                        email = document.getString("email") ?: ""
                        birthDate = document.getString("birthDate") ?: ""
                        phoneNumber = document.getString("phoneNumber") ?: ""
                        bio = document.getString("bio") ?: ""

                    }
                    isLoading = false
                }
        }
    }



    //Kullanıcı bilgilerini Firestore'a kaydeden method
    fun saveUserData() {
        // Hata durumlarını sıfırla
        emailError = false
        emailErrorMessage = null
        nameError = false
        nameErrorMessage = null
        birthDateError = false
        birthDateErrorMessage = null

        // Hata kontrolü
        var hasError = false

        if (name.isBlank()) {
            nameError = true
            nameErrorMessage = "Name cannot be empty"
            hasError = true
        }

        if (phoneNumber.isBlank()){
            phoneNumberError = true
            phoneNumberMessage= "Number cannot be empty"
            hasError = true
        }

        if (email.isBlank() || !emailPattern.matches(email.trim())) {
            emailError = true
            emailErrorMessage = "Please enter a valid email address."
            hasError = true
        }

        if (birthDate.isBlank() || !datePattern.matches(birthDate.trim())) {
            birthDateError = true
            birthDateErrorMessage = "Please enter a valid date (DD.MM.YYYY)"
            hasError = true
        }



        if (bio.isBlank()) {
            hasError = true
            bioError = true
            bioMessage = "Please enter a valid To Myself"
        }

        // Eğer hata varsa kaydetme işlemi iptal edilir
        if (hasError) return

        isSaving = true

        userId?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .collection("profile")
                .document("profile_data")
                .get()
                .addOnSuccessListener { document ->
                    val existingProfileImageUrl = document.getString("profileImageUrl")
                        ?: "https://github.com/abdullah-tanriverdi/JotterApp/raw/master/app/src/main/res/drawable/jotter_unbackground.png"

                    val user = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "birthDate" to birthDate,
                        "phoneNumber" to phoneNumber,
                        "bio" to bio,
                        "profileImageUrl" to existingProfileImageUrl
                    )

                    firestore.collection("users")
                        .document(uid)
                        .collection("profile")
                        .document("profile_data")
                        .set(user)
                        .addOnSuccessListener {
                            isSaving = false
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            isSaving = false
                            println("Error: $e")
                        }
                }
        }
    }




    Scaffold(
        //TopBar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },

        //Save FAB
        floatingActionButton = {
            FloatingActionButton(
                onClick = { saveUserData() },
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(16.dp)
            ) {

                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }

            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.height(10.dp))

            //Başlık alanı
            Text(
                text = "Update Profile Information",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )


            //Loading indicatoru
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading user data...")
            } else {
                val inputFieldModifier = Modifier.width(500.dp)


                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = nameError,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name Icon") }
                )
                if (nameError) {
                    Text(
                        text = nameErrorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                    // Email alanı
                // Email Alanı
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim()
                        emailError = email.isBlank() || !emailPattern.matches(email)
                    },
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    isError = emailError,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email Icon"
                        )
                    }
                )

// Email Hata Mesajı
                if (emailError) {
                    val errorMessage = when {
                        email.isBlank() -> "Email cannot be empty"
                        !emailPattern.matches(email) -> "Invalid email address format"
                        else -> null
                    }
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                    //Telefon numarası alanı
                    // Telefon numarası alanı
                // Telefon Numarası Regex (Örnek: +1234567890 veya 123-456-7890)
                val phonePattern = Regex("^\\+?[0-9]{7,15}\$")

// Telefon Numarası Alanı
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it.trim()
                        phoneNumberError = phoneNumber.isBlank() || !phonePattern.matches(phoneNumber)
                    },
                    label = { Text("Phone Number") },
                    placeholder = { Text("Enter your phone number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    isError = phoneNumberError,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Phone Icon"
                        )
                    }
                )

// Telefon Numarası Hata Mesajı
                if (phoneNumberError) {
                    val errorMessage = when {
                        phoneNumber.isBlank() -> "Phone number cannot be empty"
                        !phonePattern.matches(phoneNumber) -> "Invalid phone number format"
                        else -> null
                    }
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))


                    //Doğum tarihi alanı
                // Doğum Tarihi Regex (Örnek: 15.08.1995)


// Doğum Tarihi Alanı
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {
                        birthDate = it.trim()
                        birthDateError = birthDate.isBlank() || !datePattern.matches(birthDate)
                    },
                    label = { Text("Birth Date") },
                    placeholder = { Text("DD.MM.YYYY") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    ),
                    isError = birthDateError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Birth Date Icon"
                        )
                    }
                )

// Doğum Tarihi Hata Mesajı
                if (birthDateError) {
                    val errorMessage = when {
                        birthDate.isBlank() -> "Birth date cannot be empty"
                        !datePattern.matches(birthDate) -> "Please enter a valid date (DD.MM.YYYY)"
                        else -> null
                    }
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }




                Spacer(modifier = Modifier.height(16.dp))


                    //Bio alanı
                    // Bio alanı
                // Minimum Bio Uzunluğu
                val minBioLength =10
// Bio Alanı
                OutlinedTextField(
                    value = bio,
                    onValueChange = {
                        bio = it.trim()
                        bioError = bio.isBlank() || bio.length < minBioLength
                    },
                    label = { Text("To Myself") },
                    placeholder = { Text("The main idea of your life") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Bio Icon") },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    isError = bioError
                )

// Bio Hata Mesajı
                if (bioError) {
                    val errorMessage = when {
                        bio.isBlank() -> "Bio cannot be empty"
                        bio.length < minBioLength -> "Bio must be at least $minBioLength characters long"
                        else -> null
                    }
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }



            }

            }

        }
    }
