package com.tbox.jotter.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {


    // Arama  sorgusu için durum (state)
    var searchQuery by remember { mutableStateOf("") }

    //Geçerli rota bilgisi saklama (bottom bar)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    //FAB genişleme durumu
    var expanded by remember { mutableStateOf(false) }







    Scaffold(
        //BottomBar
      bottomBar = {
          BottomAppBar {

              //İkonların seçili olup olmadığına göre renk belirleme
              val homeIconTint = if (currentRoute == "home") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
              val settingIconTint = if (currentRoute == "setting") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
              val profileIconTint = if (currentRoute == "profile") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
              val graphIconTint = if (currentRoute == "graph") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

              //Profil Butonu
              IconButton(onClick = { navController.navigate("profile") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = profileIconTint)
                      Text(text = "Profile", style = MaterialTheme.typography.bodySmall, color = profileIconTint)
                  }
              }

                //Home Butonu
              IconButton(onClick = { navController.navigate("home") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Default.Home, contentDescription = "Home", tint = homeIconTint)
                      Text(text = "Home", style = MaterialTheme.typography.bodySmall, color = homeIconTint)
                  }
              }



              IconButton(onClick = { navController.navigate("setting") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.Checklist, contentDescription = "To-Do", tint = settingIconTint)
                      Text(text = "To-Do", style = MaterialTheme.typography.bodySmall, color = settingIconTint)
                  }
              }


                //Graph Butonu
              IconButton(onClick = { navController.navigate("graph") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.GridGoldenratio, contentDescription = "Graph", tint = graphIconTint)
                      Text(text = "Graph", style = MaterialTheme.typography.bodySmall, color = graphIconTint)
                  }
              }

              //Settings Butonu
              IconButton(onClick = { navController.navigate("setting") }, modifier = Modifier.weight(1f, true)) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Filled.Settings, contentDescription = "Setting", tint = settingIconTint)
                      Text(text = "Settings", style = MaterialTheme.typography.bodySmall, color = settingIconTint)
                  }
              }

          }
      },

        //FAB tanımı
      floatingActionButton = {
          Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.BottomEnd

          ){
              val rotation by animateFloatAsState(if(expanded) 45f else 0f)

              //Ana FAB
              FloatingActionButton(
                  onClick = { expanded = !expanded },
                  modifier = Modifier.padding(end = 16.dp),
                  containerColor = MaterialTheme.colorScheme.primary
              ){
                  Icon(
                      imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                      contentDescription = "Expanded FAB",
                      modifier = Modifier.rotate(rotation)
                  )
              }

              //FAB genişletiltiğinde küçük FAB butonları
              if (expanded) {
                  Column(
                      horizontalAlignment = Alignment.End,
                      verticalArrangement = Arrangement.spacedBy(12.dp),
                      modifier = Modifier
                          .padding(end = 16.dp, bottom = 80.dp)
                  ) {
                      // Yeni Not Ekleme Butonu
                      SmallFloatingActionButton(
                          onClick = {
                              expanded = false
                              navController.navigate("addNote")

                          },
                          containerColor = MaterialTheme.colorScheme.secondary
                      ) {
                          Icon(Icons.Filled.EditNote, contentDescription = "Add Note")
                      }


                      //Sesli Not Ekleme Butonu
                      SmallFloatingActionButton(
                          onClick = {

                              expanded = false
                          },
                          containerColor = MaterialTheme.colorScheme.secondary
                      ) {
                          Icon(Icons.Filled.RecordVoiceOver, contentDescription = "Voice Note")
                      }


                      //Video Not Ekleme Butonu
                      SmallFloatingActionButton(
                          onClick = {
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

        //Ana içerik
      content = {  paddingValues ->
          Column(modifier = Modifier
              .padding(16.dp)
              .fillMaxSize()

          ) {


                  // Arama Kutusu
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




                  Spacer(modifier = Modifier.height(20.dp))

              Button(
                  onClick = { println("Meet New Assistant clicked") },
                  modifier = Modifier
                      .fillMaxWidth(),
              ) {
                  // Add the chatbot icon to the button
                  Icon(
                      imageVector = Icons.Filled.Chat,
                      contentDescription = "Chatbot Icon",
                      modifier = Modifier.padding(end = 8.dp) // Adds space between the icon and text
                  )
                  Text(text = "Meet New Assistant", style = MaterialTheme.typography.bodyLarge)
              }




              Spacer(modifier = Modifier.height(20.dp))
                //Not kartlarını listeleyen yatay liste
                  LazyRow(
                      horizontalArrangement = Arrangement.spacedBy(16.dp),
                      modifier = Modifier.fillMaxWidth().padding(top = 20.dp).height(450.dp)
                  ) {


                      //Simple Note Kartı
                      item {
                          Card(
                              modifier = Modifier
                                  .width(160.dp)
                                  .clickable { navController.navigate("simpleNotes") },
                              shape = RoundedCornerShape(16.dp),

                              ) {
                              Column(
                                  modifier = Modifier
                                      .fillMaxSize()
                                      .padding(16.dp),
                                  horizontalAlignment = Alignment.CenterHorizontally,
                                  verticalArrangement = Arrangement.Center
                              ) {
                                  Icon(Icons.Filled.EditNote, contentDescription = "Simple Notes", tint = MaterialTheme.colorScheme.primary)
                                  Text("Simple Notes", style = MaterialTheme.typography.bodyLarge)
                              }
                          }
                      }

                      item {
                          Card(
                              modifier = Modifier
                                  .width(160.dp)
                                  .clickable { println("Voice Notes clicked") },
                              shape = RoundedCornerShape(16.dp),

                              ) {
                              Column(
                                  modifier = Modifier
                                      .fillMaxSize()
                                      .padding(16.dp),
                                  horizontalAlignment = Alignment.CenterHorizontally,
                                  verticalArrangement = Arrangement.Center
                              ) {
                                  Icon(Icons.Filled.RecordVoiceOver, contentDescription = "Voice Notes", tint = MaterialTheme.colorScheme.primary)
                                  Text("Voice Notes", style = MaterialTheme.typography.bodyLarge)
                              }
                          }
                      }

                      item {
                          Card(
                              modifier = Modifier
                                  .width(160.dp)
                                  .clickable { println("Video Notes clicked") },
                              shape = RoundedCornerShape(16.dp),

                              ) {
                              Column(
                                  modifier = Modifier
                                      .fillMaxSize()
                                      .padding(16.dp),
                                  horizontalAlignment = Alignment.CenterHorizontally,
                                  verticalArrangement = Arrangement.Center
                              ) {
                                  Icon(Icons.Filled.VideoCall, contentDescription = "Video Notes", tint = MaterialTheme.colorScheme.primary)
                                  Text("Video Notes", style = MaterialTheme.typography.bodyLarge)
                              }
                          }
                      }

                      item {
                          Card(
                              modifier = Modifier
                                  .width(160.dp)
                                  .clickable { println("Secret Notes clicked") },
                              shape = RoundedCornerShape(16.dp),

                              ) {
                              Column(
                                  modifier = Modifier
                                      .fillMaxSize()
                                      .padding(16.dp),
                                  horizontalAlignment = Alignment.CenterHorizontally,
                                  verticalArrangement = Arrangement.Center
                              ) {
                                  Icon(Icons.Filled.Lock, contentDescription = "Secret Notes", tint = MaterialTheme.colorScheme.primary)
                                  Text("Secret Notes", style = MaterialTheme.typography.bodyLarge)
                              }
                          }
                      }
                  }







          }
      }
  )


}








