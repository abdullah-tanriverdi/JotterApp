package com.tovaxtechnology.jotter

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EmailSignInScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Giriş linkini gönderme fonksiyonu
    fun sendSignInLink() {
        if (email.isNotBlank()) {
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://jotter-3fc11.firebaseapp.com") // Bu URL'yi kendi URL'nizle değiştirin
                .setHandleCodeInApp(true)
                .build()

            FirebaseAuth.getInstance().sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Link gönderildiğinde bilgilendirme
                        Toast.makeText(context, "Link gönderildi, e-posta adresinizi kontrol edin.", Toast.LENGTH_SHORT).show()
                        // E-posta linkini gönderdikten sonra, giriş yapılması için yönlendirme
                        navController.navigate("checkEmailLink/$email")
                    } else {
                        Toast.makeText(context, "Bir hata oluştu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "E-posta adresini girin", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Email ile Giriş Yapın", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // E-posta adresi input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta adresi") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Link gönder butonu
        Button(
            onClick = { sendSignInLink() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Linkini Gönder")
        }
    }
}


@Composable
fun CheckEmailLinkScreen(navController: NavController, email: String) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Linki kontrol etme fonksiyonu
    fun handleEmailLink(email: String) {
        val emailLink = auth.currentUser?.let {
            // Linki kontrol et
            if (auth.isSignInWithEmailLink(email)) {
                auth.signInWithEmailLink(email, email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Başarılı giriş
                            Toast.makeText(context, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                            // Ana ekrana yönlendir
                            navController.navigate("home")
                        } else {
                            Toast.makeText(context, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    LaunchedEffect(key1 = email) {
        handleEmailLink(email) // E-posta linkini kontrol et
    }

    // Yükleniyor mesajı
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("E-posta linkinizi doğruluyoruz...", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator()
    }
}

