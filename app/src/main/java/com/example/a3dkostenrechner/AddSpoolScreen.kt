package com.example.a3dkostenrechner

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpoolScreen(
    navController: NavHostController,
    materials: List<Material>,
    onAddSpool: (Spool) -> Unit
) {
    var materialName by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var initialWeight by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    val context = LocalContext.current

    if (materialName.isBlank() && materials.isNotEmpty()) {
        materialName = materials.first().name
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Neue Spule anlegen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !it }) {
                TextField(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    readOnly = true,
                    value = materialName,
                    onValueChange = { },
                    label = { Text("Material") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    materials.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                materialName = it.name
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Farbe") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = manufacturer, onValueChange = { manufacturer = it }, label = { Text("Hersteller (optional)") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = initialWeight, onValueChange = { initialWeight = it }, label = { Text("Gewicht (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = purchasePrice, onValueChange = { purchasePrice = it }, label = { Text("Preis (€)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Anzahl") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val initialW = initialWeight.replace(',', '.').toIntOrNull()
                    val price = purchasePrice.replace(',', '.').toFloatOrNull()
                    val count = quantity.toIntOrNull() ?: 1

                    if (materialName.isNotBlank() && color.isNotBlank() && initialW != null && price != null) {
                        repeat(count) {
                            onAddSpool(Spool(materialName = materialName, color = color, manufacturer = manufacturer.ifBlank { null }, initialWeight = initialW, remainingWeight = initialW, purchasePrice = price))
                        }
                        Toast.makeText(context, "$count Spule(n) hinzugefügt", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Bitte alle Pflichtfelder korrekt ausfüllen", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Spule(n) speichern")
            }
        }
    }
}
