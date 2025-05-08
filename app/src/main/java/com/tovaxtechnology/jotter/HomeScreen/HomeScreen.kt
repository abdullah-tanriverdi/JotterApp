package com.tovaxtechnology.jotter.HomeScreen

import android.annotation.SuppressLint
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen (navController: NavController) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Jotter",
                    )
                },

            )
        }

    ){

    }
}