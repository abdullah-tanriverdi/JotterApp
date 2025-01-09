package com.tbox.jotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class HomeScreen : ComponentActivity() {
    override fun onCreate (savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            HomeScreenUI()
        }
    }
}


@Composable
fun HomeScreenUI(){
    Scaffold (
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ){  innerPadding->
        Text(
            modifier = Modifier.padding(innerPadding),
            text= "Welcome"
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(text = "Jotter", style = MaterialTheme.typography.titleLarge)
        },
        actions = {


        }
    )
}


@Composable
fun BottomBar(){
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 8.dp),

        actions = {
            IconButton(onClick = {},modifier = Modifier.weight(1f, true)){ Icon(Icons.Filled.Settings, contentDescription = "Check", tint = MaterialTheme.colorScheme.primary) }
            IconButton(onClick = {},modifier = Modifier.weight(1f, true)) { Icon(Icons.Filled.AccountCircle, contentDescription = "Email",tint = MaterialTheme.colorScheme.primary) }
        }
    )
}