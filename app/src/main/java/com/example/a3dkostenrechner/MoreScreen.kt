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
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val currentVersion = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    val latestVersion by mainViewModel.latestVersion.collectAsState()
    val githubUrl = "https://github.com/3dkrvapp-droid/3d_Kostenrechner"

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_more)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.more_guide)) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("guide") }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.more_projects)) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("projects") }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.more_statistics)) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("statistics") }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.more_settings)) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("settings") }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.more_impressum)) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("impressum") }
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stringResource(R.string.more_support_dev), color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Update Sektion (NUR anzeigen wenn latestVersion nicht null ist)
            latestVersion?.let { version ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$githubUrl/releases/latest"))
                        context.startActivity(intent)
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Update, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(stringResource(R.string.more_update_available, version), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.more_download_update), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp), 
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                 Text(stringResource(R.string.more_version, currentVersion), style = MaterialTheme.typography.bodySmall)
                 TextButton(onClick = {
                     val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$githubUrl/releases"))
                     context.startActivity(intent)
                 }) {
                     Text(stringResource(R.string.more_github), style = MaterialTheme.typography.labelSmall)
                 }
            }
        }
    }
}
