package com.tbox.jotter


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Home
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
                        TopBar()
                    },
                    bottomBar = {
                        BottomBar(navController = navController)
                    }
                ) { innerPadding ->
                    AppNavigation(navController = navController, isDarkTheme = themeState, onThemeChange = { themeState = !themeState }, innerPadding = innerPadding)
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
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Jotter", style = MaterialTheme.typography.titleLarge) },
        actions = {

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

        IconButton(onClick = { navController.navigate("graph") }, modifier = Modifier.weight(1f, true)) {
            Icon(Icons.Filled.GridGoldenratio, contentDescription = "Graph", tint = MaterialTheme.colorScheme.primary)
        }
    }
}












