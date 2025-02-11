package com.tbox.jotter.profile


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenEdit (navController: NavController , userId: String?){

    //Firebase Firestore referansı
    val firestore : FirebaseFirestore = Firebase.firestore


    //Kullanıcı bilgilerini tutan değişken
    var name : String by remember { mutableStateOf("") }
    var email : String by remember { mutableStateOf("") }
    var birthDate : String by remember { mutableStateOf("") }
    var phoneNumber : String by remember { mutableStateOf("") }
    var bio : String by remember { mutableStateOf("") }


    //Birth Date için hata mesajı durumu
    var birthDateError : Boolean by remember { mutableStateOf(false) }
    var birthDateErrorMessage : String? by remember { mutableStateOf<String?>(null) }


    //Regex ile tarih formatını kontrol etme
    val datePattern = Regex("""^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.\d{4}$""")


    //Indicator durumu(Loading) firestore'dan verileri çekme
    var isLoading : Boolean by remember { mutableStateOf(true) }

    //Indicator durumu (saving) firestore'a verileri save etme
    var isSaving : Boolean by remember { mutableStateOf(false) }


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
                    isLoading  = false
                }
        }
    }



    //Kullanıcı bilgilerini Firestore'a kaydeden method
    fun saveUserData() {
        if (!datePattern.matches(birthDate.trim())) {
            birthDateError = true
            birthDateErrorMessage = "Please enter a valid date (DD.MM.YYYY)"
            return
        }
        isSaving= true
        userId?.let { uid ->
            val user = hashMapOf(
                "name" to name,
                "email" to email,
                "birthDate" to birthDate,
                "phoneNumber" to phoneNumber,
                "bio" to bio
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
                .addOnFailureListener{ e ->
                    isSaving = true
                    println("Error: $e")
                }
        }
    }




    Scaffold (

        //Save FAB
        floatingActionButton = {
            FloatingActionButton(
                onClick = { saveUserData() },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            ) {

                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }else {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }

            }
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "Update Profile Information" ,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }else {
                val inputFieldModifier = Modifier.width(500.dp)


                //İsim alanı
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = inputFieldModifier

                )

                Spacer(modifier = Modifier.height(16.dp))

                //Email alanı
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = inputFieldModifier

                )


                Spacer(modifier = Modifier.height(16.dp))

                //Telefon numarası alanı
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Telefon Numarası") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = inputFieldModifier
                )

                Spacer(modifier = Modifier.height(16.dp))


                //Doğum tarihi alanı
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {
                        birthDate = it
                        if (datePattern.matches(it.trim())) {
                            birthDateError = false
                            birthDateErrorMessage = null
                        } else {
                            birthDateError = true
                            birthDateErrorMessage = "Please enter a valid date (DD.MM.YYYY)"
                        }
                    },
                    label = { Text("Birth Date") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    placeholder = { Text("DD.MM.YYYY") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = birthDateError,
                    modifier = inputFieldModifier
                )

                birthDateErrorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = inputFieldModifier
                    )
                }



                Spacer(modifier = Modifier.height(16.dp))


                //Bio alanı
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Kendime Not") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    modifier = inputFieldModifier


                )

            }

        }

    }
}