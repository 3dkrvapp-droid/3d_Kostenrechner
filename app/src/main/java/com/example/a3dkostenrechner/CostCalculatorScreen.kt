package com.example.a3dkostenrechner

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostCalculatorScreen(
    machines: List<Machine>,
    settings: CalculationSettings,
    onSettingsChange: (CalculationSettings) -> Unit,
    spools: List<Spool>,
    onSpoolUpdate: (Spool) -> Unit,
    onAddProject: (Project) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var selectedSpool by remember(settings.useInventory, spools) { mutableStateOf(spools.firstOrNull()) }
    var materialWeight by remember { mutableStateOf("") }

    var selectedMachine by remember(machines) { mutableStateOf(machines.firstOrNull()) }
    var printDuration by remember { mutableStateOf("") }

    var totalCost by remember { mutableStateOf<Float?>(null) }

    var materialCostResult by remember { mutableStateOf<Float?>(null) }
    var machineCostResult by remember { mutableStateOf<Float?>(null) }
    var electricityResult by remember { mutableStateOf<Float?>(null) }
    var workCostResult by remember { mutableStateOf<Float?>(null) }
    var profitResult by remember { mutableStateOf<Float?>(null) }

    var showSaveProjectDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(totalCost) {
        if (totalCost != null) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    if (showSaveProjectDialog) {
        var projectName by remember { mutableStateOf("") }
        val currentSpoolForSave = selectedSpool
        if (currentSpoolForSave != null) {
            AlertDialog(
                onDismissRequest = { showSaveProjectDialog = false },
                title = { Text("Projekt speichern") },
                text = {
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { projectName = it },
                        label = { Text("Projektname") },
                        placeholder = { Text("z.B. Halterung Schreibtisch") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val weightValue = materialWeight.replace(',', '.').toFloatOrNull()
                            if (projectName.isNotBlank() && weightValue != null) {
                                onAddProject(Project(
                                    name = projectName,
                                    spool = currentSpoolForSave,
                                    materialWeight = weightValue,
                                    machine = selectedMachine,
                                    printDuration = printDuration.replace(',', '.').toFloatOrNull() ?: 0f,
                                    materialCost = materialCostResult ?: 0f,
                                    machineCost = machineCostResult ?: 0f,
                                    electricityCost = electricityResult ?: 0f,
                                    workCost = workCostResult ?: 0f,
                                    profit = profitResult ?: 0f,
                                    totalCost = totalCost ?: 0f
                                ))
                                onSpoolUpdate(currentSpoolForSave.copy(remainingWeight = (currentSpoolForSave.remainingWeight - weightValue).toInt()))

                                showSaveProjectDialog = false
                                totalCost = null // Reset the view
                                Toast.makeText(context, "Projekt '$projectName' gespeichert", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Bitte Projektnamen und Gewicht prüfen.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = projectName.isNotBlank()
                    ) { Text("Speichern & Abbuchen") }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveProjectDialog = false }) { Text("Abbrechen") }
                }
            )
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("3dkrv") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Lager verwenden", modifier = Modifier.weight(1f))
                        Switch(checked = settings.useInventory, onCheckedChange = { onSettingsChange(settings.copy(useInventory = it)) })
                    }
                    if (settings.useInventory) {
                        val currentSelectedS = selectedSpool
                        if (currentSelectedS != null) {
                            SpoolSelection(spools, currentSelectedS) { selectedSpool = it }
                        } else {
                            Text("Keine Spulen im Lager.")
                        }
                    } else {
                        OutlinedTextField(
                            value = settings.manualPricePerGram,
                            onValueChange = { onSettingsChange(settings.copy(manualPricePerGram = it)) },
                            label = { Text("Preis pro Gramm in €") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedTextField(value = materialWeight, onValueChange = { materialWeight = it }, label = { Text("Materialgewicht in g") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Drucker & Strom", style = MaterialTheme.typography.titleMedium)
                    val currentSelectedM = selectedMachine
                    if (currentSelectedM != null) {
                        MachineSelection(machines, currentSelectedM) { selectedMachine = it }
                    } else {
                        Text("Keine Maschinen hinterlegt.")
                    }
                    
                    OutlinedTextField(value = printDuration, onValueChange = { printDuration = it }, label = { Text("Druckdauer in h") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = settings.electricityRate, onValueChange = { onSettingsChange(settings.copy(electricityRate = it)) }, label = { Text("Stromkosten in €/kWh") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Arbeit & Gewinn", style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Arbeitszeit einrechnen", modifier = Modifier.weight(1f))
                        Switch(checked = settings.includeWorkingTime, onCheckedChange = { onSettingsChange(settings.copy(includeWorkingTime = it)) })
                    }
                    OutlinedTextField(value = settings.hourlyRate, onValueChange = { onSettingsChange(settings.copy(hourlyRate = it)) }, label = { Text("Stundenlohn in €") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = settings.includeWorkingTime)
                    OutlinedTextField(value = settings.workingTime, onValueChange = { onSettingsChange(settings.copy(workingTime = it)) }, label = { Text("Manuelle Arbeitszeit in h (optional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = settings.includeWorkingTime)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Gewinn einrechnen", modifier = Modifier.weight(1f))
                        Switch(checked = settings.includeProfit, onCheckedChange = { onSettingsChange(settings.copy(includeProfit = it)) })
                    }
                    OutlinedTextField(value = settings.profitMargin, onValueChange = { onSettingsChange(settings.copy(profitMargin = it)) }, label = { Text("Gewinn in % (optional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = settings.includeProfit)
                }
            }

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                coroutineScope.launch {
                    val priceToCalc = if (settings.useInventory) {
                        selectedSpool?.pricePerGram ?: 0f
                    } else {
                        settings.manualPricePerGram.replace(',', '.').toFloatOrNull() ?: 0f
                    }
                    
                    if (settings.useInventory && selectedSpool == null) {
                        Toast.makeText(context, "Keine Spule ausgewählt.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val weightToCalc = materialWeight.replace(',', '.').toFloatOrNull() ?: 0f
                    val durationToCalc = printDuration.replace(',', '.').toFloatOrNull() ?: 0f
                    val currentMachineForCalc = selectedMachine
                    val powerToCalc = currentMachineForCalc?.powerInWatts ?: 0
                    val eRateToCalc = settings.electricityRate.replace(',', '.').toFloatOrNull() ?: 0f
                    
                    val matCostVal = priceToCalc * weightToCalc
                    val machineCostVal = (currentMachineForCalc?.costPerHour ?: 0f) * durationToCalc
                    val electricityCostVal = (powerToCalc.toFloat() / 1000) * durationToCalc * eRateToCalc
                    val workCostVal = if (settings.includeWorkingTime) (settings.hourlyRate.replace(',', '.').toFloatOrNull() ?: 0f) * (settings.workingTime.replace(',', '.').toFloatOrNull() ?: 0f) else 0f
                    
                    val costBeforeProfit = matCostVal + machineCostVal + electricityCostVal + workCostVal
                    val marginVal = if (settings.includeProfit) (costBeforeProfit * (settings.profitMargin.replace(',', '.').toFloatOrNull() ?: 0f) / 100) else 0f

                    materialCostResult = matCostVal
                    machineCostResult = machineCostVal
                    electricityResult = electricityCostVal
                    workCostResult = workCostVal
                    profitResult = marginVal
                    totalCost = costBeforeProfit + marginVal
                }
            }) {
                Text("Kosten berechnen")
            }

            totalCost?.let { finalTotalCost ->
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Gesamtkosten: ${NumberFormat.getCurrencyInstance().format(finalTotalCost)}", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = { 
                        coroutineScope.launch {
                            createAndSharePdf(context, finalTotalCost, materialCostResult, machineCostResult, electricityResult, workCostResult) 
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Als PDF exportieren")
                    }
                }
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Kostenaufstellung", style = MaterialTheme.typography.titleMedium)
                        materialCostResult?.let { Text("Materialkosten: ${NumberFormat.getCurrencyInstance().format(it)}") }
                        machineCostResult?.let { Text("Maschinenkosten: ${NumberFormat.getCurrencyInstance().format(it)}") }
                        electricityResult?.let { Text("Stromkosten: ${NumberFormat.getCurrencyInstance().format(it)}") }
                        workCostResult?.let { Text("Arbeitskosten: ${NumberFormat.getCurrencyInstance().format(it)}") }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (settings.useInventory) {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        val weightToCheck = materialWeight.replace(',', '.').toFloatOrNull() ?: 0f
                        if (weightToCheck > (selectedSpool?.remainingWeight ?: 0)) {
                            Toast.makeText(context, "Nicht genügend Material auf der Spule!", Toast.LENGTH_SHORT).show()
                        } else {
                            showSaveProjectDialog = true
                        }
                    }) {
                        Text("Als Projekt speichern & Material abbuchen")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoolSelection(spools: List<Spool>, selectedSpool: Spool, onSelectionChanged: (Spool) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
            readOnly = true,
            value = "${selectedSpool.materialName} - ${selectedSpool.color} (${selectedSpool.remainingWeight}g)",
            onValueChange = { },
            label = { Text("Spule aus Lager") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            spools.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text("${selectionOption.materialName} - ${selectionOption.color} (${selectionOption.remainingWeight}g)") },
                    onClick = {
                        onSelectionChanged(selectionOption)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachineSelection(machines: List<Machine>, selectedMachine: Machine, onSelectionChanged: (Machine) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
            readOnly = true,
            value = "${selectedMachine.name} (${NumberFormat.getCurrencyInstance().format(selectedMachine.costPerHour)}/h, ${selectedMachine.powerInWatts}W)",
            onValueChange = { },
            label = { Text("Maschine") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            machines.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text("${selectionOption.name} (${NumberFormat.getCurrencyInstance().format(selectedMachine.costPerHour)}/h, ${selectedMachine.powerInWatts}W)") },
                    onClick = {
                        onSelectionChanged(selectionOption)
                        expanded = false
                    },
                )
            }
        }
    }
}
