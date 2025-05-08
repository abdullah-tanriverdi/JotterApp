package com.tovaxtechnology.jotter.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tovaxtechnology.jotter.AddToDo
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import com.tovaxtechnology.jotter.CheckEmailLinkScreen
import com.tovaxtechnology.jotter.EmailSignInScreen
import com.tovaxtechnology.jotter.HomeScreen.HomeScreen
import com.tovaxtechnology.jotter.ScreenRegistration.ScreenLogin
import com.tovaxtechnology.jotter.ScreenRegistration.ScreenReset
import com.tovaxtechnology.jotter.ScreenRegistration.ScreenSignUp


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation (
    navController: NavHostController,
    authViewModel: AuthViewModel
){
    NavHost(navController = navController ,  startDestination = "login"){



        composable("home"){
            HomeScreen(navController = navController)
        }

        composable("addToDo") {
            AddToDo(navController = navController)
        }

        composable("login"){
            ScreenLogin(navController = navController,authViewModel = authViewModel)
        }


        composable("signup"){
            ScreenSignUp(navController = navController, authViewModel = authViewModel)
        }


        composable("reset"){
            ScreenReset(navController = navController,authViewModel = authViewModel)
        }


    }
}