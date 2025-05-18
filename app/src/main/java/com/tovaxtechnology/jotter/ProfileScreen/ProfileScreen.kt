package com.tovaxtechnology.jotter.ProfileScreen

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources.Theme
import android.net.Uri
import android.os.Build
import android.text.BoringLayout
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import com.tovaxtechnology.jotter.R
import com.tovaxtechnology.jotter.ui.theme.Quicksand
import java.util.Locale



fun getSavedLocale(context: Context): Locale {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val languageCode = prefs.getString("selected_language", "en") ?: "en"
    return Locale(languageCode)
}

fun saveLocale(context: Context, locale: Locale) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("selected_language", locale.language).apply()
}

fun updateLocale(context: Context, newLocale: Locale): Context {
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(newLocale)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.createConfigurationContext(configuration)
    } else {
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        context
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController,
                  isDarkTheme: Boolean,
                  onThemeToggle: (Boolean) -> Unit,
                  authViewModel: AuthViewModel) {
    var selectedTab by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedLocale by remember { mutableStateOf(Locale("en")) }
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(Unit) {
        selectedLocale = getSavedLocale(context)
    }


    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Unauthenticated) {
            navController.navigate("splash") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.AccountDeleted) {
            navController.navigate("splash") {
                popUpTo(0) { inclusive = true }
            }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            val selectedColor = MaterialTheme.colorScheme.tertiary
            val unselectedColor = MaterialTheme.colorScheme.onTertiary
            Box {
                NavigationBar(
                    tonalElevation = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .shadow(8.dp, shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            if (selectedTab != 0) {
                                selectedTab = 0
                                navController.navigate("home") {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                modifier = Modifier.size(28.dp),
                                contentDescription = stringResource(id=R.string.home),
                                tint = if (selectedTab == 0) selectedColor else unselectedColor
                            )
                        },
                        label = {
                            Text(
                                stringResource(id=R.string.home),
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Quicksand,
                                color = if (selectedTab == 0) selectedColor else unselectedColor
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(48.dp))

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            if (selectedTab != 1) {
                                selectedTab = 1
                                if (navController.currentBackStackEntry?.destination?.route != "profile") {
                                    navController.navigate("profile") {
                                        popUpTo(0)
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PersonPin,
                                modifier = Modifier.size(28.dp),
                                contentDescription =stringResource(id= R.string.profile),
                                tint = if (selectedTab == 1) selectedColor else unselectedColor
                            )
                        },
                        label = {
                            Text(
                                stringResource(id= R.string.profile),
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Quicksand,
                                color = if (selectedTab == 1) selectedColor else unselectedColor
                            )
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = { navController.navigate("addToDo") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            modifier = Modifier.size(40.dp),
                            contentDescription = stringResource(id=R.string.addToDo)

                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            EmailInfoCard(authViewModel = authViewModel)


            ThemeSwitchCard(
                isDarkTheme = isDarkTheme,
                onToggle = { onThemeToggle(it) }
            )

            LanguageSelectionCard(
                selectedLocale = selectedLocale,
                onLanguageSelect = { locale ->
                    selectedLocale = locale
                    saveLocale(context, locale)
                    if (activity != null) {
                        activity.recreate()
                    }
                }
            )

            ChangePasswordCard(
                onPasswordChanged = {
                    Toast.makeText(context, context.getString(R.string.password_changed_success), Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )




            ContactForm()

            LogoutCard(authViewModel = authViewModel)

            DeleteAccountCard(authViewModel = authViewModel)
        }

    }
}


@Composable
fun EmailInfoCard(
    authViewModel: AuthViewModel
) {
    val email = FirebaseAuth.getInstance().currentUser?.email ?: "No Email"
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = stringResource(id = R.string.email),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.connected_email),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = if (isExpanded)
                        stringResource(id = R.string.expandless)
                    else
                        stringResource(id = R.string.expandmore)
                )


            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = email,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Quicksand,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}




@Composable
fun ContactForm() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val email = "tovax.technology@gmail.com"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){

            Icon(
                Icons.Default.ContactMail,
                contentDescription = stringResource(id = R.string.contact_us)
            )
                Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(id = R.string.contact_us),
                fontWeight = FontWeight.SemiBold,
                fontFamily = Quicksand,
                color = MaterialTheme.colorScheme.onSurface
            )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = if (expanded)
                        stringResource(id = R.string.expandless)
                    else
                        stringResource(id = R.string.expandmore)
                )
        }
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(id = R.string.name),fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                            cursorColor = MaterialTheme.colorScheme.onBackground,
                        )
                    )


                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text(stringResource(id = R.string.subject),fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                            cursorColor = MaterialTheme.colorScheme.onBackground,
                        )
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text(stringResource(id = R.string.message),fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 6,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                            cursorColor = MaterialTheme.colorScheme.onBackground,
                        )

                    )


                    Button(
                        onClick = {
                            val subjectText = subject.ifBlank { context.getString(R.string.contact_form_subject) }
                            val bodyText = "Name: $name\n\nMessage:\n$message"
                            val uriText = "mailto:$email" +
                                    "?subject=" + Uri.encode(subjectText) +
                                    "&body=" + Uri.encode(bodyText)
                            val uri = Uri.parse(uriText)
                            val intent = Intent(Intent.ACTION_SENDTO, uri)

                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, context.getString(R.string.mail_app_not_found), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Text(stringResource(id = R.string.send))
                    }
                }
            }
        }
    }
}




@Composable
fun LogoutCard(
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = stringResource(id = R.string.logout),
                    tint = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.logout),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    color = MaterialTheme.colorScheme.tertiary,

                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { showConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.logout),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.logout_confirmation_message),
                    fontWeight = FontWeight.Normal,
                    fontFamily = Quicksand,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        authViewModel.signOut()
                        Toast.makeText(context, context.getString(R.string.logged_out), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.yes),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Quicksand,
                        fontSize = 16.sp,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.no),
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}



@Composable
fun DeleteAccountCard(
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.NoAccounts,
                    contentDescription = stringResource(id = R.string.delete_account),
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.delete_account),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    showConfirmDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.delete_account),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.delete_account),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.delete_account_confirmation),
                    fontWeight = FontWeight.Normal,
                    fontFamily = Quicksand,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        authViewModel.deleteAccount()
                        Toast.makeText(context, context.getString(R.string.account_deleted), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.yes),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Quicksand,
                        fontSize = 16.sp,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.no),
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}



@Composable
fun ChangePasswordCard(
    onPasswordChanged: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }

    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    var isExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LockReset, contentDescription = stringResource(id=R.string.change_password))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id=R.string.change_password),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = if (isExpanded)
                        stringResource(id = R.string.expandless)
                    else
                        stringResource(id = R.string.expandmore)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text(
                            stringResource(id=R.string.current_password),   fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,)  },
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (currentPasswordVisible) {
                                        stringResource(id = R.string.hide_password)
                                    } else {
                                        stringResource(id = R.string.show_password)
                                    }
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                            cursorColor = MaterialTheme.colorScheme.onBackground,
                        )
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(
                            stringResource(id=R.string.new_password),   fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,) },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (newPasswordVisible) {
                                stringResource(id = R.string.hide_password)
                            } else {
                                stringResource(id = R.string.show_password)
                            }

                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = description
                                )
                            }
                        },

                                singleLine = true,
                        modifier = Modifier.fillMaxWidth(),colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                            cursorColor = MaterialTheme.colorScheme.onBackground,
                        )
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Normal,
                            fontFamily = Quicksand,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            errorMessage = null

                            if (currentPassword.isBlank() || newPassword.isBlank()) {
                                errorMessage = context.getString(R.string.fill_all)
                                return@Button
                            }

                            if (newPassword.length < 6) {
                                errorMessage = context.getString(R.string.password_min_6)
                                return@Button
                            }

                            val user = auth.currentUser
                            if (user == null || user.email.isNullOrEmpty()) {
                                errorMessage = context.getString(R.string.user_not_found)
                                return@Button
                            }

                            isLoading = true

                            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                            user.reauthenticate(credential)
                                .addOnCompleteListener { reauthTask ->
                                    if (reauthTask.isSuccessful) {
                                        user.updatePassword(newPassword)
                                            .addOnCompleteListener { updateTask ->
                                                isLoading = false
                                                if (updateTask.isSuccessful) {
                                                    onPasswordChanged()
                                                    isExpanded = false
                                                    currentPassword = ""
                                                    newPassword = ""
                                                    errorMessage = null
                                                } else {
                                                    errorMessage = updateTask.exception?.localizedMessage ?: context.getString(R.string.password_change_failed)
                                                }
                                            }
                                    } else {
                                        isLoading = false
                                        errorMessage = reauthTask.exception?.localizedMessage ?: context.getString(R.string.auth_failed)
                                    }
                                }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )  {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }  else {
                            Text(
                                stringResource(id=R.string.update_password),   fontWeight = FontWeight.SemiBold,
                                fontFamily = Quicksand,)
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun LanguageSelectionCard(
    selectedLocale: Locale,
    onLanguageSelect: (Locale) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val languageOptions = listOf(
        "Kurdî" to Locale("ku"),
        "English" to Locale("en"),
        "Türkçe" to Locale("tr"),
        "العربية" to Locale("ar"),
        "فارسی" to Locale("fa"),
        "中文" to Locale("zh"),
        "Deutsch" to Locale("de"),
        "Русский" to Locale("ur"),
        "Français" to Locale("fr"),
        "Español" to Locale("es")

    )

    val selectedLanguageName = languageOptions.find { it.second == selectedLocale }?.first ?: "English"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = stringResource(id = R.string.language_selection),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = R.string.language_selection),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Quicksand,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedLanguageName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Quicksand,
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = if (isExpanded)
                            stringResource(id = R.string.expandless)
                        else
                            stringResource(id = R.string.expandmore)
                    )


                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    languageOptions.forEach { (langName, locale) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageSelect(locale)
                                    isExpanded = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (selectedLocale == locale) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = langName,
                                fontWeight = FontWeight.Normal,
                                fontFamily = Quicksand,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun ThemeSwitchCard(
    isDarkTheme: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onToggle(!isDarkTheme) }
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.InvertColors,
                    contentDescription = stringResource(id = R.string.theme),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.theme),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Quicksand,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    contentDescription = if (isDarkTheme) stringResource(id=R.string.dark) else stringResource(id= R.string.light),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isDarkTheme) stringResource(id=R.string.dark) else stringResource(id= R.string.light),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Quicksand,
                )
            }
        }
    }
}


