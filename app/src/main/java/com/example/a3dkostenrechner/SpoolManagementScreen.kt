package com.example.a3dkostenrechner

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoolManagementScreen(
    navController: NavHostController,
    spools: List<Spool>,
    onRemoveSpool: (Spool) -> Unit,
    onUpdateSpool: (Spool) -> Unit
) {
    var spoolToEdit by remember { mutableStateOf<Spool?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lagerverwaltung") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addSpool") }) {
                Icon(Icons.Default.Add, contentDescription = "Spule hinzufügen")
            }
        }
    ) { innerPadding ->

        spoolToEdit?.let { spool ->
            EditSpoolWeightDialog(
                spool = spool,
                onDismiss = { spoolToEdit = null },
                onConfirm = {
                    onUpdateSpool(it)
                    spoolToEdit = null
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Lagerbestand (${spools.size} Spulen)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(spools, key = { it.id }) { spool ->
                val statusPercentage = (spool.remainingWeight.toFloat() / spool.initialWeight.toFloat()).coerceIn(0f, 1f)
                val statusColor = when {
                    statusPercentage > 0.25 -> Color.Green
                    statusPercentage > 0.10 -> Color(0xFFFBC02D) // Yellow
                    else -> Color.Red
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { spoolToEdit = spool }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(24.dp).background(statusColor, CircleShape))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${spool.materialName} - ${spool.color}", style = MaterialTheme.typography.titleMedium)
                            spool.manufacturer?.let { if(it.isNotBlank()) Text("Hersteller: $it", style = MaterialTheme.typography.bodySmall) }
                            Text("Rest: ${spool.remainingWeight}g / ${spool.initialWeight}g", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onRemoveSpool(spool) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Löschen")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditSpoolWeightDialog(
    spool: Spool,
    onDismiss: () -> Unit,
    onConfirm: (Spool) -> Unit
) {
    var newRemainingWeight by remember(spool.remainingWeight) { mutableStateOf(spool.remainingWeight.toString()) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restgewicht anpassen") },
        text = {
            OutlinedTextField(
                value = newRemainingWeight,
                onValueChange = { newRemainingWeight = it },
                label = { Text("Neues Restgewicht in Gramm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val weight = newRemainingWeight.replace(',', '.').toIntOrNull()
                    if (weight != null && weight <= spool.initialWeight && weight >= 0) {
                        onConfirm(spool.copy(remainingWeight = weight))
                        Toast.makeText(context, "Restgewicht aktualisiert", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ungültiges Gewicht", Toast.LENGTH_SHORT).show()
                    }
                }
            ) { Text("Speichern") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}
