package com.tovaxtechnology.jotter.Splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import com.tovaxtechnology.jotter.R
import com.tovaxtechnology.jotter.ui.theme.Quicksand
import kotlinx.coroutines.delay

class SplashScreenUI(
    private val navController: NavHostController,
    private val authViewModel: AuthViewModel,
) {

    @Composable
    fun SplashScreen() {

        val authState by authViewModel.authState.observeAsState(AuthViewModel.AuthState.Loading)


        LaunchedEffect(Unit) {
            delay(2000L)


            navigateBasedOnAuthState(authState)
        }

        SplashContent()
    }


    private fun navigateBasedOnAuthState(authState: AuthViewModel.AuthState) {
        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
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

            }
        }
    }


    @Composable
    private fun SplashContent() {
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

                LogoSection()

                BrandText()
            }
        }
    }


    @Composable
    private fun LogoSection() {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "App Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }


    @Composable
    private fun BrandText() {
        Text(
            text = stringResource(id = R.string.brand_name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Quicksand,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.padding(bottom = 60.dp)
        )
    }
}


