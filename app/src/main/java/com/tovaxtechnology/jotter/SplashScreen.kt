package com.tovaxtechnology.jotter



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.observeAsState(AuthViewModel.AuthState.Loading)

    // 2 saniye bekleyip yönlendirme işlemi yapacağız
    LaunchedEffect(Unit) {
        delay(2000) // Splash ekranını 2 saniye gösterme

        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true } // Splash ekranını stack'ten çıkar
                    launchSingleTop = true
                }
            }
            is AuthViewModel.AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthViewModel.AuthState.EmailNotVerified -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> {
                // Loading durumunda hiçbir şey yapılmaz
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Orta içerik: Logo + Jotter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "App Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                
            }

            // En alttan biraz yukarıda duran Tovax Technology yazısı
            Text(
                text = "Tovax Technology",
                fontSize = 13.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.padding(bottom = 60.dp) // ALT BOŞLUK DEĞİŞTİ
            )
        }
    }
}
