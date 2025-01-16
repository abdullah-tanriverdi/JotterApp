package com.tbox.jotter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController , authViewModel: AuthViewModel){

    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        delay(2000)
        when(authState) {
            is AuthViewModel.AuthState.Authenicated -> {
                navController.navigate("home"){
                    popUpTo("splash") { inclusive=true }
                }
            }

            is AuthViewModel.AuthState.Unauthenticated, null -> {
                navController.navigate("login"){
                    popUpTo("splash"){ inclusive=true }
                }
            }

            else -> {

            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center

    ){
        Image(
            painter = painterResource(id = R.drawable.jotter_unbackground), // Görselin id'si
            contentDescription = "Logo", // Görselin açıklaması
            modifier = Modifier.wrapContentSize() // Görsel boyutunun içeriğe uygun olmasını sağlar
        )
    }

}