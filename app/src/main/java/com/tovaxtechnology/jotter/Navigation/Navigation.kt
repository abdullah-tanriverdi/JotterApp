package com.tovaxtechnology.jotter.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tovaxtechnology.jotter.AddTodoScreen
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import com.tovaxtechnology.jotter.Auth.ScreenLogin
import com.tovaxtechnology.jotter.Auth.ScreenReset
import com.tovaxtechnology.jotter.Auth.ScreenSignUp
import com.tovaxtechnology.jotter.HomeScreen.HomeScreen
import com.tovaxtechnology.jotter.ProfileScreen
import com.tovaxtechnology.jotter.SplashScreenUI
import com.tovaxtechnology.jotter.UpdateTodoScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation (
    navController: NavHostController,
    authViewModel: AuthViewModel
){


    NavHost(navController = navController ,  startDestination = "splash"){

        composable("splash"){
            SplashScreenUI(navController = navController , authViewModel = authViewModel).SplashScreen()
        }

        composable("home"){
            HomeScreen(navController = navController, authViewModel= authViewModel)
        }

        composable("profile"){
            ProfileScreen(navController= navController)
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


        composable("addToDo") {
          AddTodoScreen(navController = navController , authViewModel = authViewModel)
        }


        composable("updateToDo/{todoId}"){
            backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId") ?: ""
            UpdateTodoScreen(todoId = todoId, navController = navController)

        }


    }
}