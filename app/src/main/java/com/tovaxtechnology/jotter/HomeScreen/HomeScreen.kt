package com.tovaxtechnology.jotter.HomeScreen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.LocalDate.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen (navController: NavController) {

    var currentDate by remember { mutableStateOf( LocalDate.now()) }

    val formatter = DateTimeFormatter.ofPattern("dd MMMM")

    val previous1 = currentDate.minusDays(1)
    val previous2 = currentDate.minusDays(2)
    val nextDay1 = currentDate.plusDays(1)
    val nextDay2 = currentDate.plusDays(2)


    LaunchedEffect(currentDate) {
        currentDate = LocalDate.now()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Jotter",
                    )
                },

            )
        },

        bottomBar = {
            BottomAppBar {

                IconButton(onClick = { }, modifier = Modifier.weight(1f, true)) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                }

                IconButton(onClick = { navController.navigate("addToDo") }, modifier = Modifier.weight(1f, true)) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Profile")
                    }

                IconButton(onClick = { }, modifier = Modifier.weight(1f, true)) {
                        Icon(Icons.Filled.Settings, contentDescription = "Profile")
                }

            }
        },

        floatingActionButton = { ExtendedFloatingActionButton(
            onClick = {

            }
        ) {
            Icon(Icons.Filled.KeyboardArrowUp, "ChatBot")
            Text("Chatbot")
        }
        },


        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                val scrollState = rememberScrollState()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),  // Horizontal kaydırma
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateCard(date = previous1.format(formatter))
                    DateCard(date = previous2.format(formatter))
                    DateCard(date = "Today")
                    DateCard(date = nextDay1.format(formatter))
                    DateCard(date = nextDay2.format(formatter))

                }
            }
        }


    )
}




@Composable
fun DateCard(date: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date,
            )
        }
    }
}
