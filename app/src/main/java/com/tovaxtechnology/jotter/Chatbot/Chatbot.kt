package com.tovaxtechnology.jotter.Chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tovaxtechnology.jotter.R
import com.tovaxtechnology.jotter.ui.theme.Quicksand
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chatbot( viewModel: ChatViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    var userMessage by remember { mutableStateOf("") }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource( id = R.string.app_name ),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Quicksand,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 12.dp),
                reverseLayout = true
            ) {
                items(chatHistory.reversed()) { message ->
                    ChatBubble(message)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userMessage,
                    onValueChange = {
                        userMessage = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.message_write),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Quicksand
                        )
                    },
                    singleLine = true,
                    enabled = true
                )
                IconButton(
                    onClick = {
                        if (userMessage.isNotBlank()) {
                            val msgToSend = userMessage.trim()
                            userMessage = ""
                            viewModel.sendMessage(msgToSend)
                        }
                    },
                    enabled = userMessage.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = stringResource(id = R.string.send))
                }

            }
        }
    }
}



@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.onSurface

    val shape = if (message.isUser) {
        RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp)
    }

    val timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .shadow(4.dp, shape)
                    .background(bubbleColor, shape)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeText,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
