package com.tovaxtechnology.jotter.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tovaxtechnology.jotter.HomeScreen.HomeScreen


@Composable
fun Navigation (
    navController: NavHostController,
){
    NavHost(navController = navController ,  startDestination = "home"){

        composable("home"){
            HomeScreen(navController = navController)
        }


    }
}