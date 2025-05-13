package com.tovaxtechnology.jotter

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jotter",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val selectedColor = Color(0xFF2196F3)
            val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                                navController.navigate("home")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                modifier = Modifier.size(28.dp),
                                contentDescription = "Home",
                                tint = if (selectedTab == 0) selectedColor else unselectedColor
                            )
                        },
                        label = {
                            Text(
                                "Home",
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
                                    navController.navigate("profile")
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PersonPin,
                                modifier = Modifier.size(28.dp),
                                contentDescription = "Profile",
                                tint = if (selectedTab == 1) selectedColor else unselectedColor
                            )
                        },
                        label = {
                            Text(
                                "Profile",
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
                        containerColor = Color(0xFF4CAF50),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Add Todo"
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
            ProfileOptionList(
                userEmail = userEmail,
                onLogout = {
                    showLogoutDialog = true
                },
                onDeleteAccount = {
                    showDeleteAccountDialog = true                },
                onChangeTheme = { showThemeDialog = true },
                onChangeLanguage = {
                   showLanguageDialog = true
                },
                onChangePassword = {
                    showChangePasswordDialog = true
                },
                onNotificationToggle = {
                    showNotificationDialog = true

                }
            )

            if (showThemeDialog) {
                ThemeSelectionDialog(onDismiss = { showThemeDialog = false })
            }

            if (showLanguageDialog) {
                LanguageSelectionDialog(onDismiss = { showLanguageDialog = false })
            }

            if (showNotificationDialog) {
                NotificationSettingsDialog(onDismiss = { showNotificationDialog = false })
            }

            if (showChangePasswordDialog) {
                ChangePasswordDialog(
                    onDismiss = { showChangePasswordDialog = false },
                    userEmail = userEmail
                )
            }

            if (showLogoutDialog) {
                LogoutConfirmationDialog(
                    onDismiss = { showLogoutDialog = false },
                    onConfirmLogout = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                )
            }

            if (showDeleteAccountDialog) {
                DeleteAccountDialog(
                    onDismiss = { showDeleteAccountDialog = false },
                    userEmail = userEmail,
                    onAccountDeleted = {
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                )
            }

            

        }
    }
}

@Composable
fun ProfileOptionList(
    userEmail: String,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onChangeTheme: () -> Unit,
    onChangeLanguage: () -> Unit,
    onChangePassword: () -> Unit,
    onNotificationToggle: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Account Info
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor =  Color(0xFFC8E6C9)
        )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }



            }
        }

        val options = listOf(
            ProfileOption("Change Theme", Icons.Default.ColorLens, Color.Black, onChangeTheme),
            ProfileOption("Language", Icons.Default.Language, Color.Black, onChangeLanguage),
            ProfileOption("Change Password", Icons.Default.Lock, Color.Black, onChangePassword),
            ProfileOption("Notifications", Icons.Default.Notifications, Color.Black, onNotificationToggle),
            ProfileOption("Logout", Icons.Default.ExitToApp, MaterialTheme.colorScheme.error, onLogout),
            ProfileOption("Delete My Account", Icons.Default.Delete, MaterialTheme.colorScheme.error, onDeleteAccount)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options) { option ->
                ProfileOptionItem(
                    text = option.text,
                    icon = option.icon,
                    onClick = option.action,
                    color = option.color,
                    modifier = Modifier.height(80.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        
    }
}





@Composable
fun ProfileOptionItem(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color =  Color(0xFFBBDEFB),
        tonalElevation = 4.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color  = color
            )
        }
    }
}


data class ProfileOption(
    val text: String,
    val icon: ImageVector?,
    val color: Color,
    val action: () -> Unit
)

@Composable
fun ThemeSelectionDialog(
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf("System Default") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE3F2FD),
        title = {
            Text(
                text = "Choose Theme",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black // Başlık siyah
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                val options = listOf("Light Mode", "Dark Mode")

                options.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTheme = theme }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == theme,
                            onClick = { selectedTheme = theme },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF388E3C), // Mavi seçili durum
                                unselectedColor = Color(0xFF757575) // Gri seçili olmayan durum
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = theme,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                            color = Color.Black // Yazılar siyah
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "OK",
                    color = Color(0xFF388E3C),
                )
            }
        },
        shape = RoundedCornerShape(16.dp), // Köşe yuvarlatma
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf("Türkçe") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE3F2FD),
        title = {
            Text(
                text = "Dil Seçimi",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                val options = listOf("Türkçe", "Kürtçe", "Arapça")

                options.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguage = language }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == language,
                            onClick = { selectedLanguage = language },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF388E3C), // Yeşil seçili
                                unselectedColor = Color(0xFF757575)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = language,
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
                    text = "Tamam",
                    color = Color(0xFF388E3C)
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
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



