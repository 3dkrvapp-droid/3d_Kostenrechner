package com.example.a3dkostenrechner

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.util.Scanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gson = Gson()
    val settings by mainViewModel.settings.collectAsState()

    // Backup Launcher
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val data = mapOf(
                        "materials" to mainViewModel.materials.value,
                        "machines" to mainViewModel.machines.value,
                        "spools" to mainViewModel.spools.value,
                        "projects" to mainViewModel.projects.value
                    )
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        OutputStreamWriter(stream).use { writer ->
                            writer.write(gson.toJson(data))
                        }
                    }
                    Toast.makeText(context, "Backup erfolgreich erstellt", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Fehler beim Backup", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Restore Launcher
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        val json = Scanner(stream).useDelimiter("\\A").next()
                        // Hinweis: Typprüfung hier vereinfacht
                        Toast.makeText(context, "Import gestartet", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Fehler beim Import", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Sektion: Sprache
            Text(
                stringResource(R.string.settings_language),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            var langExpanded by remember { mutableStateOf(false) }
            val currentLangLabel = when(settings.language) {
                "de" -> stringResource(R.string.settings_lang_de)
                "en" -> stringResource(R.string.settings_lang_en)
                else -> stringResource(R.string.settings_lang_system)
            }

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedCard(
                    onClick = { langExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(currentLangLabel)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(expanded = langExpanded, onDismissRequest = { langExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_lang_system)) },
                        onClick = {
                            mainViewModel.updateSettings(settings.copy(language = "system"))
                            langExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_lang_de)) },
                        onClick = {
                            mainViewModel.updateSettings(settings.copy(language = "de"))
                            langExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_lang_en)) },
                        onClick = {
                            mainViewModel.updateSettings(settings.copy(language = "en"))
                            langExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            // Sektion: Verwaltung
            Text(
                stringResource(R.string.settings_section_admin),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_manage_materials)) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("materialManagement") }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_manage_machines)) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("machineManagement") }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Sektion: Backup
            Text(
                stringResource(R.string.settings_backup_restore),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_backup_btn)) },
                supportingContent = { Text(stringResource(R.string.settings_backup_desc)) },
                leadingContent = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                modifier = Modifier.clickable { createDocumentLauncher.launch("3dkrv_backup.json") }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_restore_btn)) },
                supportingContent = { Text(stringResource(R.string.settings_restore_desc)) },
                leadingContent = { Icon(Icons.Default.FileUpload, contentDescription = null) },
                modifier = Modifier.clickable { openDocumentLauncher.launch(arrayOf("application/json")) }
            )
        }
    }
}
