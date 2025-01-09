package com.tbox.jotter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tbox.jotter.ui.theme.JotterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isDarkTheme = isDarkMode()
        setContent {
            var themeState by remember { mutableStateOf(isDarkTheme) }

            JotterTheme(darkTheme = themeState) {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopBar(isDarkTheme = themeState, onThemeChange = { themeState = !themeState })
                    },
                    bottomBar = {
                        BottomBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(navController, startDestination = "home") {
                        composable("home") {
                            MainScreen(navController = navController, innerPadding = innerPadding)
                        }
                        composable("settings") {
                            SettingsScreen(navController = navController, innerPadding = innerPadding)
                        }
                        composable("profile") {
                            ProfileScreen(navController = navController, innerPadding = innerPadding)
                        }
                    }
                }
            }
        }
    }

    private fun isDarkMode(): Boolean {
        val uiMode = resources.configuration.uiMode
        return uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}








@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(isDarkTheme: Boolean, onThemeChange: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Jotter", style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = { onThemeChange() }) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}




@Composable
fun BottomBar(navController: NavController) {
    BottomAppBar {
        IconButton(onClick = { navController.navigate("settings") }, modifier = Modifier.weight(1f, true)) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
        }

        IconButton(onClick =  { navController.navigate("home")}, modifier = Modifier.weight(1f,true)){
            Icon(Icons.Default.Home, contentDescription = "Home", tint= MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = { navController.navigate("profile") }, modifier = Modifier.weight(1f, true)) {
            Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = MaterialTheme.colorScheme.primary)
        }
    }
}


@Composable
fun MainScreen(
    navController: NavController,
    innerPadding: PaddingValues
) {
    Text(
        modifier = Modifier.padding(innerPadding),
        text = "Welcome to Jotter App"
    )
}

@Composable
fun SettingsScreen(
    navController: NavController,
    innerPadding: PaddingValues
) {
    Text(
        modifier = Modifier.padding(innerPadding),
        text = "Settings Screen"
    )
}


@Composable
fun ProfileScreen(
    navController: NavController,
    innerPadding: PaddingValues
) {
    Text(
        modifier = Modifier.padding(innerPadding),
        text = "Profile Screen"
    )
}




