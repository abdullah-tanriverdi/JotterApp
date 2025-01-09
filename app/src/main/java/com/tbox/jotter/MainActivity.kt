package com.tbox.jotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
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
import com.tbox.jotter.ui.theme.JotterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            JotterTheme (darkTheme = isDarkTheme){
                MainScreen(isDarkTheme){
                    isDarkTheme = !isDarkTheme}
                }

            }
        }
    }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit // Tema değiştirme işlevi ana ekrana iletiliyor
) {

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Jotter", style = MaterialTheme.typography.titleLarge)

                },
                actions = {
                    IconButton(onClick = {
                        onThemeChange()
                    }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Arka plan rengi
                    titleContentColor = MaterialTheme.colorScheme.onPrimary, // Başlık rengi
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Aksiyon ikon rengi
                )
            )
        }

    ){  innerPadding->
        Text(
            modifier = Modifier.padding(innerPadding),
            text= "Welcome"
        ) }

   
}




