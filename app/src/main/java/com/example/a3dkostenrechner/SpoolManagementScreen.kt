package com.example.a3dkostenrechner

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    
    val sortedSpools = remember(spools) {
        spools.sortedBy { it.materialName }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addSpool") }) {
                Icon(Icons.Default.Add, contentDescription = null)
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
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.inventory_title, spools.size),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(sortedSpools, key = { it.id }) { spool ->
                val statusPercentage = if (spool.initialWeight > 0) {
                    (spool.remainingWeight.toFloat() / spool.initialWeight.toFloat()).coerceIn(0f, 1f)
                } else 0f
                
                val statusColor = when {
                    statusPercentage > 0.25 -> Color.Green
                    statusPercentage > 0.10 -> Color(0xFFFBC02D)
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
                            spool.manufacturer?.let { if(it.isNotBlank()) Text(it, style = MaterialTheme.typography.bodySmall) }
                            Text(
                                stringResource(R.string.inventory_rest, spool.remainingWeight, spool.initialWeight),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { onRemoveSpool(spool) }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
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
    val spoolName = "${spool.materialName} - ${spool.color}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.inventory_edit_weight, spoolName)) },
        text = {
            OutlinedTextField(
                value = newRemainingWeight,
                onValueChange = { newRemainingWeight = it },
                label = { Text(stringResource(R.string.inventory_new_weight_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val weight = newRemainingWeight.replace(',', '.').toIntOrNull()
                    if (weight != null && weight >= 0) {
                        onConfirm(spool.copy(remainingWeight = weight))
                    } else {
                        Toast.makeText(context, "Ungültiges Gewicht", Toast.LENGTH_SHORT).show()
                    }
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}
