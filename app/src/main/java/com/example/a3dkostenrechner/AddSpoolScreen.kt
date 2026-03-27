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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpoolScreen(
    navController: NavHostController,
    materials: List<Material>,
    spools: List<Spool>,
    onAddSpool: (Spool) -> Unit,
    onUpdateSpool: (Spool) -> Unit
) {
    var materialName by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var initialWeight by remember { mutableStateOf("1000") }
    var purchasePrice by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    val context = LocalContext.current

    var showMergeDialog by remember { mutableStateOf<Spool?>(null) }

    val manufacturerSuggestions = remember(spools) {
        spools.mapNotNull { it.manufacturer }.distinct().sorted()
    }
    
    val colorSuggestions = remember(spools) {
        spools.map { it.color }.distinct().sorted()
    }

    LaunchedEffect(materials) {
        if (materialName.isBlank() && materials.isNotEmpty()) {
            materialName = materials.first().name
        }
    }

    val spoolToMerge = showMergeDialog
    if (spoolToMerge != null) {
        AlertDialog(
            onDismissRequest = { showMergeDialog = null },
            title = { Text(stringResource(R.string.dialog_merge_title)) },
            text = { Text(stringResource(R.string.dialog_merge_message)) },
            confirmButton = {
                Button(onClick = {
                    val addedWeight = initialWeight.toIntOrNull() ?: 0
                    val addedPrice = purchasePrice.replace(',', '.').toFloatOrNull() ?: 0f
                    // Korrektur: Gewicht UND Preis addieren
                    onUpdateSpool(spoolToMerge.copy(
                        remainingWeight = spoolToMerge.remainingWeight + addedWeight,
                        initialWeight = spoolToMerge.initialWeight + addedWeight,
                        purchasePrice = spoolToMerge.purchasePrice + addedPrice
                    ))
                    showMergeDialog = null
                    navController.popBackStack()
                }) { Text(stringResource(R.string.btn_merge)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    val initialW = initialWeight.toIntOrNull() ?: 1000
                    val price = purchasePrice.replace(',', '.').toFloatOrNull() ?: 0f
                    onAddSpool(Spool(materialName = materialName, color = color, manufacturer = manufacturer.ifBlank { null }, initialWeight = initialW, remainingWeight = initialW, purchasePrice = price))
                    showMergeDialog = null
                    navController.popBackStack()
                }) { Text(stringResource(R.string.btn_create_new)) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_spool_title)) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var materialExpanded by remember { mutableStateOf(false) }

            if (materials.isEmpty()) {
                OutlinedTextField(
                    value = stringResource(R.string.error_no_materials),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    label = { Text(stringResource(R.string.label_material)) }
                )
            } else {
                ExposedDropdownMenuBox(
                    expanded = materialExpanded,
                    onExpandedChange = { materialExpanded = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(),
                        readOnly = true,
                        value = materialName,
                        onValueChange = { },
                        label = { Text(stringResource(R.string.label_material)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = materialExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = materialExpanded,
                        onDismissRequest = { materialExpanded = false }
                    ) {
                        materials.forEach { material ->
                            DropdownMenuItem(
                                text = { Text(material.name) },
                                onClick = {
                                    materialName = material.name
                                    materialExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            var colorExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = colorExpanded,
                onExpandedChange = { colorExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth(),
                    value = color,
                    onValueChange = { 
                        color = it
                        colorExpanded = it.isNotBlank()
                    },
                    label = { Text(stringResource(R.string.label_color)) },
                    trailingIcon = { 
                        if (colorSuggestions.isNotEmpty()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) 
                        }
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                val filteredColorSuggestions = colorSuggestions.filter { it.contains(color, ignoreCase = true) }
                if (filteredColorSuggestions.isNotEmpty()) {
                    ExposedDropdownMenu(expanded = colorExpanded, onDismissRequest = { colorExpanded = false }) {
                        filteredColorSuggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
                                onClick = {
                                    color = suggestion
                                    colorExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            var manufacturerExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = manufacturerExpanded,
                onExpandedChange = { manufacturerExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth(),
                    value = manufacturer,
                    onValueChange = { 
                        manufacturer = it
                        manufacturerExpanded = it.isNotBlank()
                    },
                    label = { Text(stringResource(R.string.label_manufacturer)) },
                    trailingIcon = { 
                        if (manufacturerSuggestions.isNotEmpty()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = manufacturerExpanded) 
                        }
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                val filteredSuggestions = manufacturerSuggestions.filter { it.contains(manufacturer, ignoreCase = true) }
                if (filteredSuggestions.isNotEmpty()) {
                    ExposedDropdownMenu(expanded = manufacturerExpanded, onDismissRequest = { manufacturerExpanded = false }) {
                        filteredSuggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
                                onClick = {
                                    manufacturer = suggestion
                                    manufacturerExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = initialWeight, onValueChange = { initialWeight = it }, label = { Text(stringResource(R.string.label_weight)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = purchasePrice, onValueChange = { purchasePrice = it }, label = { Text(stringResource(R.string.label_price)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text(stringResource(R.string.label_quantity)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val initialW = initialWeight.replace(',', '.').toIntOrNull()
                    val price = purchasePrice.replace(',', '.').toFloatOrNull()
                    val count = quantity.toIntOrNull() ?: 1

                    if (materialName.isNotBlank() && color.isNotBlank() && initialW != null && price != null) {
                        val existing = spools.find { 
                            it.materialName == materialName && 
                            it.color == color && 
                            it.manufacturer == manufacturer.ifBlank { null } &&
                            it.initialWeight == initialW
                        }

                        if (existing != null) {
                            showMergeDialog = existing
                        } else {
                            repeat(count) {
                                onAddSpool(Spool(materialName = materialName, color = color, manufacturer = manufacturer.ifBlank { null }, initialWeight = initialW, remainingWeight = initialW, purchasePrice = price))
                            }
                            Toast.makeText(context, context.getString(R.string.toast_spools_added, count), Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.toast_fill_all), Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_save_spool))
            }
        }
    }
}
