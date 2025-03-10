package com.tbox.jotter.ScreenPermission

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("JotterPrefs", Context.MODE_PRIVATE)
    val permissionsGrantedBefore = sharedPreferences.getBoolean("permissionsGranted", false)

    var allPermissionsGranted by remember { mutableStateOf(permissionsGrantedBefore) }
    var showIntroScreen by remember { mutableStateOf(!permissionsGrantedBefore) }
    var permissionDenied by remember { mutableStateOf(false) } // 📌 Kullanıcı izinleri reddettiğinde gösterilecek

    //  Gerekli izinler listesi (Android sürümüne göre)
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    //
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        allPermissionsGranted = permissions.all { it.value }

        if (allPermissionsGranted) {
            sharedPreferences.edit().putBoolean("permissionsGranted", true).apply()
            navController.navigate("splash") { //  İzinler verildiyse, splash ekranına git
                popUpTo("permissions") { inclusive = true }
            }
        } else {
            permissionDenied = true //  Kullanıcı izinleri reddettiğinde ekranda uyarı göster
        }
    }

    if (permissionsGrantedBefore) {
        //  Eğer izinler daha önce verilmişse, direkt `splash` ekranına yönlendir
        LaunchedEffect(Unit) {
            navController.navigate("splash") {
                popUpTo("permissions") { inclusive = true }
            }
        }
    } else {
        //  Kullanıcı hala intro ekranındaysa göster
        if (showIntroScreen) {
            PermissionIntroScreen { showIntroScreen = false }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Permission Request",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "To continue using Jotter, please grant the necessary permissions.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                //  İzinleri tekrar isteme butonu
                Button(
                    onClick = { launcher.launch(requiredPermissions) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Grant Permissions")
                }

                if (permissionDenied) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "⚠️ You denied the permissions! Please grant them to continue.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                //  Çıkış Butonu
                OutlinedButton(
                    onClick = { navController.popBackStack() }, // Kullanıcı çıkış yapabilir
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Exit", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun PermissionIntroScreen(onContinueClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //  Başlık
        Text(
            "Permissions Needed",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Açıklama
        Text(
            "To fully use Jotter, we need access to:\n\n" +
                    "📷 Camera - To take a profile photo\n" +
                    "🖼️ Gallery - To select a profile photo\n\n" +
                    "Click Continue to grant these permissions.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        //  Devam Et Butonu
        Button(
            onClick = onContinueClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Text("Continue")
        }
    }
}
