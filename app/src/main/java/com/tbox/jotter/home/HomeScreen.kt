package com.tbox.jotter.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var expanded by remember { mutableStateOf(false) }



  Scaffold(
      bottomBar = {
          BottomAppBar {

              val homeIconTint = if (currentRoute == "home") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
              val settingIconTint = if (currentRoute == "setting") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
              val profileIconTint = if (currentRoute == "profile") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
              val graphIconTint = if (currentRoute == "graph") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary


              IconButton(onClick = { navController.navigate("home") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Default.Home, contentDescription = "Home", tint = homeIconTint)
                      Text(text = "Home", style = MaterialTheme.typography.bodySmall, color = homeIconTint)
                  }
              }


              IconButton(onClick = { navController.navigate("setting") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.Settings, contentDescription = "Setting", tint = settingIconTint)
                      Text(text = "Settings", style = MaterialTheme.typography.bodySmall, color = settingIconTint)
                  }
              }


              IconButton(onClick = { navController.navigate("profile") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = profileIconTint)
                      Text(text = "Profile", style = MaterialTheme.typography.bodySmall, color = profileIconTint)
                  }
              }


              IconButton(onClick = { navController.navigate("graph") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.GridGoldenratio, contentDescription = "Graph", tint = graphIconTint)
                      Text(text = "Graph", style = MaterialTheme.typography.bodySmall, color = graphIconTint)
                  }
              }

          }
      },
      floatingActionButton = {
          Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.BottomEnd
          ){
              FloatingActionButton(
                  onClick = { expanded = !expanded },
                  modifier = Modifier.padding(16.dp),
                  containerColor = MaterialTheme.colorScheme.primary
              ){
                  Icon(
                      imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                      contentDescription = "Expanded FAB"
                  )
              }

              if (expanded) {
                  Column(
                      horizontalAlignment = Alignment.End,
                      verticalArrangement = Arrangement.spacedBy(12.dp),
                      modifier = Modifier
                          .padding(end = 16.dp, bottom = 80.dp)
                  ) {
                      SmallFloatingActionButton(
                          onClick = {
                              expanded = false
                              
                          },
                          containerColor = MaterialTheme.colorScheme.secondary
                      ) {
                          Icon(Icons.Filled.Edit, contentDescription = "Add Note")
                      }

                      SmallFloatingActionButton(
                          onClick = {
                              println("Seçenek 2 seçildi")
                              expanded = false
                          },
                          containerColor = MaterialTheme.colorScheme.secondary
                      ) {
                          Icon(Icons.Filled.RecordVoiceOver, contentDescription = "Voice Note")
                      }

                      SmallFloatingActionButton(
                          onClick = {
                              println("Seçenek 3 seçildi")
                              expanded = false
                          },
                          containerColor = MaterialTheme.colorScheme.secondary
                      ) {
                          Icon(Icons.Filled.VideoCall, contentDescription = "Video Notes")
                      }
                  }
              }

          }
      },
      content = {  paddingValues ->
          Column(modifier = Modifier
              .padding(16.dp)
              .fillMaxSize()
          ) {

              OutlinedTextField(
                  value = searchQuery,
                  onValueChange = { searchQuery = it },
                  label = { Text("Search Notes") },
                  modifier = Modifier
                      .fillMaxWidth()
                      .padding(top = 40.dp),
                  singleLine = true,
                  leadingIcon = {
                      Icon(
                          imageVector = Icons.Default.Search,
                          contentDescription = "Search Icon",
                      )
                  },
                  shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(20.dp)) // Arama kutusunun altındaki boşluk
                  Text(text = "No Notes Yet", style = MaterialTheme.typography.titleLarge)


          }
      }
  )


}





