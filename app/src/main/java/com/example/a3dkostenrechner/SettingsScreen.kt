package com.example.a3dkostenrechner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settings: CalculationSettings,
    onSettingsChange: (CalculationSettings) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Theme Selection
            var themeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = themeExpanded, onExpandedChange = { themeExpanded = !it }, modifier = Modifier.padding(16.dp)) {
                TextField(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    readOnly = true,
                    value = settings.appTheme.name,
                    onValueChange = {},
                    label = { Text("App-Design") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) }
                )
                ExposedDropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                    AppTheme.values().forEach {
                        DropdownMenuItem(text = { Text(it.name) }, onClick = {
                            onSettingsChange(settings.copy(appTheme = it))
                            themeExpanded = false
                        })
                    }
                }
            }

            // Management Links
            ListItem(
                headlineContent = { Text("Materialien verwalten") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("materialManagement") }
            )
            ListItem(
                headlineContent = { Text("Maschinen verwalten") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate("machineManagement") }
            )
        }
    }
}
