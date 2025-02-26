package com.tbox.jotter.ScreenSettings

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenSettings(navController: NavController,darkTheme : Boolean, onThemeUpdated: () -> Unit){


    //Mevcut bottom bar rota bilgisini alır
    val currentRoute : String? = navController.currentBackStackEntryAsState().value?.destination?.route


    //Firebase referansı
    val auth = FirebaseAuth.getInstance()

    //Sign Out Diyalog state değişkeni
    var showDialogSignOut by remember { mutableStateOf(false) }


    //Diyalog kısmı
    if (showDialogSignOut) {
        LogoutDialog(
            onConfirm = {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onDismiss = { showDialogSignOut = false }
        )
    }

    Scaffold(

        //TopBar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content ={
                 paddingValues ->


            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                //Light/Dark kartı
                item {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onThemeUpdated() },
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                if (darkTheme) {
                                    Text("Dark Theme")
                                } else {
                                    Text("Light Theme")
                                }
                            }

                            val size = 70.dp
                            val iconSize = 24.dp
                            val offset by animateDpAsState(
                                targetValue = if (darkTheme) 0.dp else size,
                                animationSpec = tween(durationMillis = 300)
                            )

                            Box(modifier = Modifier.width(size * 2)) {
                                Box(
                                    modifier = Modifier
                                        .size(size)
                                        .offset(x = offset)
                                        .padding(10.dp)
                                        .clip(shape = CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {}

                                Row(
                                    modifier = Modifier
                                        .border(
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = CircleShape
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier.size(size),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(iconSize),
                                            imageVector = Icons.Default.Nightlight,
                                            contentDescription = "Night Mode Icon",
                                            tint = if (darkTheme) MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier.size(size),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(iconSize),
                                            imageVector = Icons.Default.LightMode,
                                            contentDescription = "Light Mode Icon",
                                            tint = if (darkTheme) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }



                //Log Out kartı
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.error),
                        onClick = {
                            showDialogSignOut = true
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Log Out")
                            IconButton(onClick = {
                                showDialogSignOut = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Log Out"
                                )
                            }


                        }
                    }
                }





            }
        },
        //Bottom bar
        bottomBar = {
            BottomAppBar {

                //İkonların seçili olup olmadığına göre renk belirleme
                val homeIconTint = if (currentRoute == "home") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                val settingIconTint = if (currentRoute == "setting") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                val profileIconTint = if (currentRoute == "profile") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                val graphIconTint = if (currentRoute == "graph") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

                //Profil Butonu
                IconButton(onClick = {   if (currentRoute != "profile") {
                    navController.navigate("profile")
                } },
                    modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = profileIconTint)
                        Text(text = "Profile", style = MaterialTheme.typography.bodySmall, color = profileIconTint)
                    }
                }

                //Home Butonu
                IconButton(onClick = {
                    navController.navigate("home")
                }, modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = homeIconTint)
                        Text(text = "Home", style = MaterialTheme.typography.bodySmall, color = homeIconTint)
                    }
                }

                
                //Settings Butonu
                IconButton(onClick = { if (currentRoute !="setting"){
                    navController.navigate("setting") }
                }
                   , modifier = Modifier.weight(1f, true)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Settings, contentDescription = "Setting", tint = settingIconTint)
                        Text(text = "Settings", style = MaterialTheme.typography.bodySmall, color = settingIconTint)
                    }
                }

            }
        }




    )

}




//Alert Dialog Compose'u
@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 6.dp,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Text(
                text = "Are you sure you want to log out?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Log Out", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}
