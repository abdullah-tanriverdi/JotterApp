package com.tbox.jotter.signup


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tbox.jotter.AuthViewModel



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController ,
    authViewModel: AuthViewModel
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

            LazyColumn(
                modifier =  Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    TitleSignUp()
                    Spacer(modifier = Modifier.height(32.dp))
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

                            EmailInputFieldSignUp(email, onValueChange = { email = it })

                            Spacer(modifier = Modifier.height(16.dp))

                            PasswordInputFieldSignUp(password, onValueChange = { password = it })
                        }
                    }
                }


                item {
                    SignUpButtonSignUp(email, password, authViewModel, authState, navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    LoginRouteButtonSignUp(navController)

                }



            }
        }


    }

}

@Composable
fun TitleSignUp(){
    Text(
        text = "Kayıt Ol",
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 36.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInputFieldSignUp(email : String , onValueChange : (String) -> Unit){
    OutlinedTextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text("E-Posta") },
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large.copy(CornerSize(24.dp)))
            .background(Color.Transparent),
        singleLine = true,
        shape = MaterialTheme.shapes.large.copy(CornerSize(24.dp)),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "E-Posta İkonu",
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInputFieldSignUp(password: String, onValueChange: (String) -> Unit) {

    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        label = {
            Text(
                text = "Parola",

            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large.copy(CornerSize(24.dp)))
            .background(Color.Transparent),
        singleLine = true,
        shape = MaterialTheme.shapes.large.copy(CornerSize(24.dp)),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Parola İkonu",
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
fun SignUpButtonSignUp (
    email : String ,
    password : String ,
    authViewModel : AuthViewModel ,
    authState: State<AuthViewModel.AuthState?> ,
    navController: NavController,
) {

    val context = LocalContext.current
    Button(
        onClick = {

            when {
                email.isBlank() || password.isBlank() -> {
                    Toast.makeText( context , "Lütfen tüm alanları doldurun." , Toast.LENGTH_SHORT).show()
                }
                !isValidEmail(email) -> {
                    Toast.makeText(context , "Lütfen geçerli bir e-posta adresi girin" , Toast.LENGTH_SHORT).show()
                }

                else -> {
                    authViewModel.signup(email , password)
                    Toast.makeText(context , "E-posta adresinizi doğrulayın" , Toast.LENGTH_SHORT).show()
                }
            }

        },
        enabled = authState.value != AuthViewModel.AuthState.Loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)

    ) {
        if (authState.value == AuthViewModel.AuthState.Loading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Hesap Oluştur",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

    }


}

@Composable
fun LoginRouteButtonSignUp(navController: NavController){
    TextButton(
        onClick = {
            navController.navigate("login"){
                popUpTo("signup") { inclusive= true }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Zaten Hesabın Var Mı? Giriş Yap!",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


fun isValidEmail(email  : String ) : Boolean {
    return  android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}




