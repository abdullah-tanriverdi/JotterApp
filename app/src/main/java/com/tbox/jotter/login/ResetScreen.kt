package com.tbox.jotter.login


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tbox.jotter.auth.AuthViewModel
import com.tbox.jotter.signup.isValidEmail


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()

    Scaffold(
        topBar = {
                TopAppBar(
                    title = { Text(text = "Jotter", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = {navController.popBackStack()}) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = "Geri",

                                )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,

                        )
                )

        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ){
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )  {



                item {
                    TitleReset()

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        tonalElevation = 8.dp,
                        shadowElevation = 4.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            EmailInputFieldReset(email , onValueChange = { email = it })



                        }
                    }
                }

                item {
                    ResetButtonReset(email,authViewModel,authState,navController)
                }
            }
        }


    }

}

@Composable
fun TitleReset(){
    Text(
        text = "Şifre Sıfırlama",
        fontSize = 36.sp,
        color = MaterialTheme.colorScheme.onPrimary
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInputFieldReset(email: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text("E-Posta")},
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large.copy(CornerSize(24.dp)))
            .background(Color.Transparent),
        singleLine = true,
        shape = MaterialTheme.shapes.large.copy(CornerSize(24.dp)),

        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "E-Posta Ikonu",
                tint = MaterialTheme.colorScheme.primary

            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor =MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary
        )

    )
}

@Composable
fun ResetButtonReset(email: String, authViewModel: AuthViewModel, authState: State<AuthViewModel.AuthState?>, navController: NavController){
    val context = LocalContext.current

    Button(
        onClick =  {
            when{
                email.isBlank() -> {
                    Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()

                }
                !isValidEmail(email) -> {
                    Toast.makeText(context,"Lütfen geçerli e-posta girin", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    authState.value == AuthViewModel.AuthState.Loading
                    authViewModel.resetPassword(email)
                    Toast.makeText(context, "E-Postanıza gelen bağlantı üzerinden şifrenizi sıfırlayabilirsiniz", Toast.LENGTH_SHORT).show()
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        enabled = authState.value != AuthViewModel.AuthState.Loading
    ) {
        if (authState.value == AuthViewModel.AuthState.Loading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Bağlantı Gönder",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

}

