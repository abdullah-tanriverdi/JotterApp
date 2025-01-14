package com.tbox.jotter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    val systemTheme = isSystemInDarkTheme() // Sistem temasına duyarlı hale getirmek için
    val currentTheme = remember { mutableStateOf(isDarkTheme || systemTheme) }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dark Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    currentTheme.value = !currentTheme.value
                    onThemeChange() // Kullanıcının seçimine göre tema değiştir
                }
                .padding(8.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (currentTheme.value) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = if (currentTheme.value) "Switch to Light Mode" else "Switch to Dark Mode",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (currentTheme.value) "Switch to Light Mode" else "Switch to Dark Mode",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
