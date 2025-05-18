package com.tovaxtechnology.jotter.Auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tovaxtechnology.jotter.R
import com.tovaxtechnology.jotter.ui.theme.Quicksand


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSignUp(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val authState = authViewModel.authState.observeAsState()

    val context = LocalContext.current


    if (authState.value == AuthViewModel.AuthState.SignUpSuccess) {
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

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
                actions = {
                    IconButton(
                        onClick = {
                            //eklenecek
                        }
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(id = R.string.info),
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id=R.string.sign_up),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(id = R.string.email)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = stringResource(id = R.string.email),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },

                            isError = email.isNotEmpty() &&  !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()


                        )


                        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = stringResource(id= R.string.email_error),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.invalid_email),
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Quicksand,
                                )
                            }
                        }



                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = password,
                            onValueChange = { password = it},
                            label = { Text(stringResource(id = R.string.password)) },
                            singleLine = true,
                            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = stringResource(id = R.string.password),
                                    tint = MaterialTheme.colorScheme.primary

                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                    Icon(
                                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisibility) stringResource(id= R.string.visibility) else stringResource(id= R.string.visibility_off),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            isError = password.isNotEmpty() && password.length < 6
                        )


                        if (password.isNotEmpty() && password.length < 6) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = stringResource(id= R.string.password_error),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(id = R.string.invalid_password),
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Quicksand,
                                )
                            }
                        }




                        Spacer(modifier = Modifier.height(8.dp))


                        Button(
                            onClick = {
                                when {
                                    email.isBlank() || password.isBlank() -> {
                                    }
                                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                    }
                                    else -> {
                                        authViewModel.signup(email, password)
                                    }
                                }
                            },
                            enabled = authState.value != AuthViewModel.AuthState.Loading,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),

                        ) {
                            if (authState.value == AuthViewModel.AuthState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = stringResource(id= R.string.create_account),)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        stringResource(id= R.string.have_account),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Quicksand,
                        color = MaterialTheme.colorScheme.onTertiary,
                    )

                }

                TextButton(
                    onClick = {  navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    } }

                ) {
                    Text(
                        stringResource(id=R.string.return_login_screen),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Quicksand,
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                }

            }
        }


    }
}