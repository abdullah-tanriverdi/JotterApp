package com.tovaxtechnology.jotter.ProfileScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources.Theme
import android.os.Build
import android.text.BoringLayout
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
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
                  onThemeToggle: (Boolean) -> Unit) {
    var selectedTab by remember { mutableStateOf(1) }
    var selectedLanguage by remember { mutableStateOf("English") }
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedLocale by remember { mutableStateOf(Locale("en")) }

    LaunchedEffect(Unit) {
        selectedLocale = getSavedLocale(context)
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

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
        "العربية" to Locale("ar")
    )

    // Burada seçilen locale göre dil adını bulalım
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedLanguageName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
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

@Composable
fun NotificationSettingsDialog(
    onDismiss: () -> Unit
) {
    var selectedOption by remember { mutableStateOf("Evet") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE3F2FD), // Yumuşak kırmızımsı ton
        title = {
            Text(
                text = "Bildirim Ayarları",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                val options = listOf("Evet", "Hayır")

                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF388E3C),
                                unselectedColor = Color(0xFF757575)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                            color = Color.Black
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Kaydet",
                    color = Color(0xFF388E3C), // Mavi onay butonu
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    userEmail: String
) {
    val context = LocalContext.current
    var isLinkSent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE3F2FD),
        title = {
            Text(
                text = "Şifre Değiştir",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        },
        text = {
            if (!isLinkSent) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Şifrenizi sıfırlamak için e-posta adresinize bir bağlantı göndermek istiyor musunuz?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "E-posta: $userEmail",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                Text(
                    text = "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!isLinkSent) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    isLinkSent = true
                                    Toast.makeText(context, "Bağlantı gönderildi.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Bağlantı gönderilemedi.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = if (isLinkSent) "Kapat" else "Bağlantı Gönder",
                    color = Color(0xFF388E3C)
                )
            }
        },
        dismissButton = {
            if (!isLinkSent) {
                TextButton(onClick = onDismiss) {
                    Text(text = "İptal", color = Color.Red)
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE3F2FD),
        title = {
            Text(
                text = "Çıkış Yap",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        },
        text = {
            Text(
                text = "Hesabınızdan çıkış yapmak istediğinize emin misiniz?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmLogout()
                onDismiss()
            }) {
                Text(text = "Evet", color = Color(0xFF388E3C))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Hayır", color = Color.Red)
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onAccountDeleted: () -> Unit,
    userEmail: String
) {
    val context = LocalContext.current
    var isDeleting by remember { mutableStateOf(false) }
    var deletionSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE3F2FD),
        title = {
            Text(
                text = "Hesabı Sil",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        },
        text = {
            if (!deletionSuccess) {
                Text(
                    text = "Bu işlem geri alınamaz. Gerçekten hesabınızı silmek istiyor musunuz?\n\nE-posta: $userEmail",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            } else {
                Text(
                    text = "Hesabınız başarıyla silindi.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        },
        confirmButton = {
            if (!deletionSuccess) {
                TextButton(
                    onClick = {
                        if (!isDeleting) {
                            isDeleting = true
                            FirebaseAuth.getInstance().currentUser?.delete()
                                ?.addOnCompleteListener { task ->
                                    isDeleting = false
                                    if (task.isSuccessful) {
                                        deletionSuccess = true
                                        Toast.makeText(context, "Hesap silindi", Toast.LENGTH_SHORT).show()
                                        onAccountDeleted()
                                    } else {
                                        Toast.makeText(context, "Silinemedi. Yeniden giriş yapmanız gerekebilir.", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    }
                ) {
                    Text("Evet, Sil", color = MaterialTheme.colorScheme.error)
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Kapat", color = Color(0xFF388E3C))
                }
            }
        },
        dismissButton = {
            if (!deletionSuccess) {
                TextButton(onClick = onDismiss) {
                    Text("İptal", color = Color.Gray)
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}



