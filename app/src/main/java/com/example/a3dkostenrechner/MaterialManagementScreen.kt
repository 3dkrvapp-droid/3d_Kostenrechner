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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import java.text.DecimalFormat
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialManagementScreen(
    navController: NavHostController,
    materials: List<Material>,
    onAddMaterial: (Material) -> Unit,
    onRemoveMaterial: (Material) -> Unit
) {
    var newMaterialName by remember { mutableStateOf("") }
    var newMaterialPrice by remember { mutableStateOf("") } // Price per gram

    var spoolWeight by remember { mutableStateOf("1000") }
    var spoolPrice by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Materialien verwalten") },
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
                        Text("Neues Material anlegen", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = newMaterialName,
                            onValueChange = { newMaterialName = it },
                            label = { Text("Materialname") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Preis aus Spule berechnen (optional)", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = spoolWeight,
                                onValueChange = { spoolWeight = it },
                                label = { Text("Gewicht (g)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = spoolPrice,
                                onValueChange = { spoolPrice = it },
                                label = { Text("Preis (€)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                val weight = spoolWeight.replace(',', '.').toFloatOrNull()
                                val price = spoolPrice.replace(',', '.').toFloatOrNull()
                                if (weight != null && price != null && weight > 0) {
                                    val pricePerGram = price / weight
                                    newMaterialPrice = DecimalFormat("0.00000").format(pricePerGram)
                                } else {
                                    Toast.makeText(context, "Ungültige Eingabe für Spulen-Berechnung", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Preis pro Gramm berechnen")
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        OutlinedTextField(
                            value = newMaterialPrice,
                            onValueChange = { newMaterialPrice = it },
                            label = { Text("Preis pro Gramm in €") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val pricePerGram = newMaterialPrice.replace(',', '.').toFloatOrNull()
                                if (newMaterialName.isNotBlank() && pricePerGram != null) {
                                    onAddMaterial(Material(newMaterialName, pricePerGram))
                                    newMaterialName = ""
                                    newMaterialPrice = ""
                                    spoolPrice = ""
                                    Toast.makeText(context, "Material hinzugefügt", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Ungültige Eingabe. Name und Preis pro Gramm dürfen nicht leer sein.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Material hinzufügen")
                        }
                    }
                }
            }

            items(materials, key = { it.name }) { material ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currencyFormat = NumberFormat.getCurrencyInstance().apply {
                        minimumFractionDigits = 3
                        maximumFractionDigits = 5
                    }
                    Text("${material.name} (${currencyFormat.format(material.pricePerGram)}/g)")
                    IconButton(onClick = { onRemoveMaterial(material) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Löschen")
                    }
                }
            }
        }
    }
}
