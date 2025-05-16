package com.tovaxtechnology.jotter

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import com.tovaxtechnology.jotter.Navigation.Navigation
import com.tovaxtechnology.jotter.ProfileScreen.ProfileScreen
import com.tovaxtechnology.jotter.ProfileScreen.updateLocale
import com.tovaxtechnology.jotter.ui.theme.JotterTheme
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("selected_language", "en") ?: "en"
        val localeUpdatedContext = updateLocale(newBase, Locale(languageCode))
        super.attachBaseContext(localeUpdatedContext)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {

            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            JotterTheme(
            darkTheme = isDarkTheme
            ) {
                val navController = rememberNavController()
                Navigation(
                    navController = navController,
                    authViewModel = AuthViewModel(),
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = it }
                )
            }
        }

    }




}

