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
import androidx.compose.ui.res.stringResource
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
    var materialWeightInput by remember { mutableStateOf("") }

    var selectedMachine by remember(machines) { mutableStateOf(machines.firstOrNull()) }
    var printDurationInput by remember { mutableStateOf("") }

    var totalCost by remember { mutableStateOf<Float?>(null) }

    var materialCostResult by remember { mutableStateOf<Float?>(null) }
    var machineCostResult by remember { mutableStateOf<Float?>(null) }
    var electricityResult by remember { mutableStateOf<Float?>(null) }
    var workCostResult by remember { mutableStateOf<Float?>(null) }
    var profitResult by remember { mutableStateOf<Float?>(null) }

    var showSaveProjectDialog by remember { mutableStateOf(false) }
    var projectNameInput by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(totalCost) {
        if (totalCost != null) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    if (showSaveProjectDialog) {
        val spoolToSave = selectedSpool
        if (spoolToSave != null) {
            AlertDialog(
                onDismissRequest = { showSaveProjectDialog = false },
                title = { Text(stringResource(R.string.more_projects)) },
                text = {
                    OutlinedTextField(
                        value = projectNameInput,
                        onValueChange = { projectNameInput = it },
                        label = { Text("Projektname") },
                        placeholder = { Text("z.B. Halterung Schreibtisch") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val parsedWeight = materialWeightInput.replace(',', '.').toFloatOrNull()
                            if (projectNameInput.isNotBlank() && parsedWeight != null) {
                                onAddProject(Project(
                                    name = projectNameInput,
                                    spool = spoolToSave,
                                    materialWeight = parsedWeight,
                                    machine = selectedMachine,
                                    printDuration = printDurationInput.replace(',', '.').toFloatOrNull() ?: 0f,
                                    materialCost = materialCostResult ?: 0f,
                                    machineCost = machineCostResult ?: 0f,
                                    electricityCost = electricityResult ?: 0f,
                                    workCost = workCostResult ?: 0f,
                                    profit = profitResult ?: 0f,
                                    totalCost = totalCost ?: 0f
                                ))
                                onSpoolUpdate(spoolToSave.copy(remainingWeight = (spoolToSave.remainingWeight - parsedWeight).toInt()))

                                showSaveProjectDialog = false
                                totalCost = null
                                Toast.makeText(context, "Projekt '$projectNameInput' gespeichert", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = projectNameInput.isNotBlank()
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveProjectDialog = false }) { Text("Abbrechen") }
                }
            )
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) }
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
                        Text(stringResource(R.string.calc_use_inventory), modifier = Modifier.weight(1f))
                        Switch(checked = settings.useInventory, onCheckedChange = { onSettingsChange(settings.copy(useInventory = it)) })
                    }
                    if (settings.useInventory) {
                        val currentSpool = selectedSpool
                        if (currentSpool != null) {
                            SpoolSelection(spools, currentSpool) { selectedSpool = it }
                        } else {
                            Text("Keine Spulen im Lager.")
                        }
                    } else {
                        OutlinedTextField(
                            value = settings.manualPricePerGram,
                            onValueChange = { onSettingsChange(settings.copy(manualPricePerGram = it)) },
                            label = { Text(stringResource(R.string.calc_manual_price)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedTextField(value = materialWeightInput, onValueChange = { materialWeightInput = it }, label = { Text(stringResource(R.string.calc_weight)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.calc_section_machine), style = MaterialTheme.typography.titleMedium)
                    val currentMachine = selectedMachine
                    if (currentMachine != null) {
                        MachineSelection(machines, currentMachine) { selectedMachine = it }
                    } else {
                        Text(stringResource(R.string.calc_no_machines))
                    }
                    
                    OutlinedTextField(value = printDurationInput, onValueChange = { printDurationInput = it }, label = { Text(stringResource(R.string.calc_duration)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = settings.electricityRate, onValueChange = { onSettingsChange(settings.copy(electricityRate = it)) }, label = { Text(stringResource(R.string.calc_electricity_rate)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.calc_section_work), style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.calc_include_work), modifier = Modifier.weight(1f))
                        Switch(checked = settings.includeWorkingTime, onCheckedChange = { onSettingsChange(settings.copy(includeWorkingTime = it)) })
                    }
                    OutlinedTextField(value = settings.hourlyRate, onValueChange = { onSettingsChange(settings.copy(hourlyRate = it)) }, label = { Text(stringResource(R.string.calc_hourly_rate)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = settings.includeWorkingTime)
                    OutlinedTextField(value = settings.workingTime, onValueChange = { onSettingsChange(settings.copy(workingTime = it)) }, label = { Text(stringResource(R.string.calc_work_time)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = settings.includeWorkingTime)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.calc_include_profit), modifier = Modifier.weight(1f))
                        Switch(checked = settings.includeProfit, onCheckedChange = { onSettingsChange(settings.copy(includeProfit = it)) })
                    }
                    OutlinedTextField(value = settings.profitMargin, onValueChange = { onSettingsChange(settings.copy(profitMargin = it)) }, label = { Text(stringResource(R.string.calc_profit_margin)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = settings.includeProfit)
                }
            }

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                coroutineScope.launch {
                    val pricePerGram = if (settings.useInventory) {
                        selectedSpool?.pricePerGram ?: 0f
                    } else {
                        settings.manualPricePerGram.replace(',', '.').toFloatOrNull() ?: 0f
                    }
                    
                    if (settings.useInventory && selectedSpool == null) {
                        Toast.makeText(context, "Keine Spule ausgewählt.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val weight = materialWeightInput.replace(',', '.').toFloatOrNull() ?: 0f
                    val duration = printDurationInput.replace(',', '.').toFloatOrNull() ?: 0f
                    val machine = selectedMachine
                    val powerWatts = machine?.powerInWatts ?: 0
                    val eRate = settings.electricityRate.replace(',', '.').toFloatOrNull() ?: 0f
                    
                    val matCost = pricePerGram * weight
                    val mCost = (machine?.costPerHour ?: 0f) * duration
                    val eCost = (powerWatts.toFloat() / 1000) * duration * eRate
                    val wCost = if (settings.includeWorkingTime) (settings.hourlyRate.replace(',', '.').toFloatOrNull() ?: 0f) * (settings.workingTime.replace(',', '.').toFloatOrNull() ?: 0f) else 0f
                    
                    val costBeforeProfit = matCost + mCost + eCost + wCost
                    val margin = if (settings.includeProfit) (costBeforeProfit * (settings.profitMargin.replace(',', '.').toFloatOrNull() ?: 0f) / 100) else 0f

                    materialCostResult = matCost
                    machineCostResult = mCost
                    electricityResult = eCost
                    workCostResult = wCost
                    profitResult = margin
                    totalCost = costBeforeProfit + margin
                }
            }) {
                Text(stringResource(R.string.calc_btn_calculate))
            }

            totalCost?.let { costValue ->
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${stringResource(R.string.calc_total_cost)}: ${NumberFormat.getCurrencyInstance().format(costValue)}", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = { 
                        coroutineScope.launch {
                            createAndSharePdf(context, projectNameInput.ifBlank { null }, costValue, materialCostResult, machineCostResult, electricityResult, workCostResult, profitResult)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                }
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(R.string.calc_breakdown), style = MaterialTheme.typography.titleMedium)
                        materialCostResult?.let { Text("${stringResource(R.string.calc_mat_cost)}: ${NumberFormat.getCurrencyInstance().format(it)}") }
                        machineCostResult?.let { Text("${stringResource(R.string.calc_mach_cost)}: ${NumberFormat.getCurrencyInstance().format(it)}") }
                        electricityResult?.let { Text("${stringResource(R.string.calc_elec_cost)}: ${NumberFormat.getCurrencyInstance().format(it)}") }
                        workCostResult?.let { Text("${stringResource(R.string.calc_work_cost)}: ${NumberFormat.getCurrencyInstance().format(it)}") }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (settings.useInventory) {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        val checkW = materialWeightInput.replace(',', '.').toFloatOrNull() ?: 0f
                        if (checkW > (selectedSpool?.remainingWeight ?: 0)) {
                            Toast.makeText(context, "Nicht genügend Material auf der Spule!", Toast.LENGTH_SHORT).show()
                        } else {
                            showSaveProjectDialog = true
                        }
                    }) {
                        Text(stringResource(R.string.calc_btn_save_project))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoolSelection(spools: List<Spool>, currentSelectedSpool: Spool, onSelectionChanged: (Spool) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
            readOnly = true,
            value = "${currentSelectedSpool.materialName} - ${currentSelectedSpool.color} (${currentSelectedSpool.remainingWeight}g)",
            onValueChange = { },
            label = { Text(stringResource(R.string.nav_inventory)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            spools.forEach { spoolOption ->
                DropdownMenuItem(
                    text = { Text("${spoolOption.materialName} - ${spoolOption.color} (${spoolOption.remainingWeight}g)") },
                    onClick = {
                        onSelectionChanged(spoolOption)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachineSelection(machines: List<Machine>, currentSelectedMachine: Machine, onSelectionChanged: (Machine) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
            readOnly = true,
            value = "${currentSelectedMachine.name} (${NumberFormat.getCurrencyInstance().format(currentSelectedMachine.costPerHour)}/h, ${currentSelectedMachine.powerInWatts}W)",
            onValueChange = { },
            label = { Text(stringResource(R.string.calc_section_machine)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            machines.forEach { machineOption ->
                DropdownMenuItem(
                    text = { Text("${machineOption.name} (${NumberFormat.getCurrencyInstance().format(machineOption.costPerHour)}/h, ${machineOption.powerInWatts}W)") },
                    onClick = {
                        onSelectionChanged(machineOption)
                        expanded = false
                    },
                )
            }
        }
    }
}
