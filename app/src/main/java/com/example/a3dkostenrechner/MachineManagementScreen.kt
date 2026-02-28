package com.example.a3dkostenrechner

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachineManagementScreen(
    navController: NavHostController,
    machines: List<Machine>,
    onAddMachine: (Machine) -> Unit,
    onRemoveMachine: (Machine) -> Unit
) {
    var newMachineName by remember { mutableStateOf("") }
    var newMachineCost by remember { mutableStateOf("") }
    var newMachinePower by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maschinen verwalten") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newMachineName,
                            onValueChange = { newMachineName = it },
                            label = { Text("Maschinenname") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newMachineCost,
                            onValueChange = { newMachineCost = it },
                            label = { Text("Kosten pro Stunde in €") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newMachinePower,
                            onValueChange = { newMachinePower = it },
                            label = { Text("Stromverbrauch in Watt") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                val cost = newMachineCost.replace(',', '.').toFloatOrNull()
                                val power = newMachinePower.replace(',', '.').toIntOrNull()
                                if (newMachineName.isNotBlank() && cost != null && power != null) {
                                    onAddMachine(Machine(newMachineName, cost, power))
                                    newMachineName = ""
                                    newMachineCost = ""
                                    newMachinePower = ""
                                    Toast.makeText(context, "Maschine hinzugefügt", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Ungültige Eingabe. Alle Felder müssen korrekt ausgefüllt sein.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Maschine hinzufügen")
                        }
                    }
                }
            }

            items(machines, key = { it.name }) { machine ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${machine.name} (${NumberFormat.getCurrencyInstance().format(machine.costPerHour)}/h, ${machine.powerInWatts}W)")
                    IconButton(onClick = { onRemoveMachine(machine) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Löschen")
                    }
                }
            }
        }
    }
}
