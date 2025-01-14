package com.tbox.jotter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(
    navController: NavController,

) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar(navController = navController)
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = "Expand FAB"
                    )
                }

                if (expanded) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(end = 16.dp, bottom = 80.dp) // Daha düzgün hizalama için padding eklendi
                    ) {
                        SmallFloatingActionButton(
                            onClick = {
                                expanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Seçenek 1")
                        }

                        SmallFloatingActionButton(
                            onClick = {
                                println("Seçenek 2 seçildi")
                                expanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "Seçenek 2")
                        }

                        SmallFloatingActionButton(
                            onClick = {
                                println("Seçenek 3 seçildi")
                                expanded = false
                            },
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Seçenek 3")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Text(
            modifier = Modifier.padding(innerPadding),
            text = "Welcome to Jotter App"
        )
    }
}
