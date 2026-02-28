package com.example.a3dkostenrechner

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(navController: NavHostController) {
    val context = LocalContext.current
    val versionName by produceState(initialValue = "N/A") {
        value = withContext(Dispatchers.IO) {
            try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "N/A"
            } catch (e: Exception) {
                "N/A"
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mehr") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Links
            ListItem(
                headlineContent = { Text("Anleitung") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("guide") }
            )
            ListItem(
                headlineContent = { Text("Gespeicherte Projekte") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("projects") }
            )
            ListItem(
                headlineContent = { Text("Statistiken") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("statistics") }
            )
            ListItem(
                headlineContent = { Text("Einstellungen") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("settings") }
            )
            ListItem(
                headlineContent = { Text("Impressum") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("impressum") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Donate Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/3dkrv"))
                    context.startActivity(intent)
                }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = "Spenden", tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Entwicklung unterstützen", color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // App Info
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                 Text("Version: $versionName", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
