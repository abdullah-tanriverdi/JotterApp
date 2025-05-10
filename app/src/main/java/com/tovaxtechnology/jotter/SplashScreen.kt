package com.tovaxtechnology.jotter



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.observeAsState(AuthViewModel.AuthState.Loading)

    // 2 saniye bekleyip yönlendirme işlemi yapacağız
    LaunchedEffect(Unit) {
        delay(1000) // Splash ekranını 2 saniye gösterme

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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Splash ekranında gösterilecek logo veya yazı
        Text("Jotter")
    }
}
