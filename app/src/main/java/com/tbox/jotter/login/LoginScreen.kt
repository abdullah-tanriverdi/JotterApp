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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tbox.jotter.AuthViewModel
import com.tbox.jotter.signup.EmailInputFieldSignUp
import com.tbox.jotter.signup.PasswordInputFieldSignUp
import com.tbox.jotter.signup.isValidEmail

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()


    LaunchedEffect(authState.value) {
        if (authState.value is AuthViewModel.AuthState.Authenicated){
            navController.navigate("home") {
                popUpTo("login") { inclusive= true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Jotter", style = MaterialTheme.typography.titleLarge) },
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    TitleLogin()
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
                            EmailInputFieldLogin(email , onValueChange = { email = it })

                            Spacer(modifier = Modifier.height(16.dp))

                            PasswordInputFieldLogin(password , onValueChange = { password = it })

                            Spacer(modifier = Modifier.height(16.dp))


                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    LoginButtonLogin(authState, email , password, authViewModel,navController)


                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpRouteButtonLogin(navController)

                }

                item {

                    Spacer(modifier = Modifier.height(10.dp))

                    ResetRouteButtonLogin(navController)


                }

            }
        }


    }

}


@Composable
fun TitleLogin(){
    Text(
        text = "Giriş Yap",
        fontSize = 36.sp,
        color = MaterialTheme.colorScheme.onPrimary
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInputFieldLogin(email : String , onValueChange : (String) -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInputFieldLogin(password: String, onValueChange: (String) -> Unit) {
    var passwordVisibility by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        label = { Text("Parola" )},
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large.copy(CornerSize(24.dp)))
            .background(Color.Transparent),
        singleLine = true,
        shape = MaterialTheme.shapes.large.copy(CornerSize(24.dp)),
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Kilit",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisibility) "Şifreyi gizle" else "Şifreyi göster",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
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
fun LoginButtonLogin(authState : State<AuthViewModel.AuthState?>, email: String, password: String, authViewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current

    Button(
        onClick = {
            when {
                email.isBlank() || password.isBlank() -> {
                    Toast.makeText( context , "Lütfen tüm alanları doldurun" , Toast.LENGTH_SHORT).show()
                }
                !isValidEmail(email) -> {
                    Toast.makeText( context , "Lütfen geçerli e-posta girin" , Toast.LENGTH_SHORT).show()
                }
                else -> {
                    authViewModel.login(email , password)


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
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onBackground)
        } else {
            Text(
                text = "Giriş Yap",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthViewModel.AuthState.Authenicated -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is AuthViewModel.AuthState.Error -> {
                val errorMessage = (authState.value as AuthViewModel.AuthState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {
                // Diğer durumlar (Loading, Unauthenticated vs.)
            }
        }
    }}

@Composable
fun SignUpRouteButtonLogin(navController: NavController) {
    TextButton(
        onClick = {
            navController.navigate("signup")
                  }, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hesabın Yok Mu? Hemen Kayıt Ol!",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
fun ResetRouteButtonLogin(navController: NavController) {
    TextButton(
        onClick = {
            navController.navigate("reset")
        }, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Şifremi Unuttum",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}