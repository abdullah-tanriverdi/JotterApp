package com.tbox.jotter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun GraphScreen(
    innerPadding: PaddingValues
) {
    Text(
        modifier = Modifier.padding(innerPadding),
        text = "Graph Screen"
    )
}
