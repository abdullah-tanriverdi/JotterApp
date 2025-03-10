package com.tbox.jotter.splash

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.tbox.jotter.R
import com.tbox.jotter.auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(Unit) {
        delay(500) // Splash süresi

        when (authState) {
            is AuthViewModel.AuthState.Authenicated -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is AuthViewModel.AuthState.Unauthenticated, null -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.jotter_unbackground),
            contentDescription = "Logo",
            modifier = Modifier.wrapContentSize()
        )
    }
}
