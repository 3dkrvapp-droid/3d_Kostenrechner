package com.example.a3dkostenrechner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    navController: NavHostController,
    project: Project?
) {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "Projektdetails") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (project == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Text("Projekt nicht gefunden.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Berechnet am: ${dateFormat.format(Date(project.date))}", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- Details ---
            Text("Berechnungsdetails", style = MaterialTheme.typography.titleLarge)
            project.spool?.let { Text("Spule: ${it.materialName} (${it.color})", style = MaterialTheme.typography.bodyLarge) }
            Text("Materialverbrauch: ${project.materialWeight}g", style = MaterialTheme.typography.bodyLarge)
            project.machine?.let { Text("Drucker: ${it.name}", style = MaterialTheme.typography.bodyLarge) }
            Text("Druckdauer: ${project.printDuration}h", style = MaterialTheme.typography.bodyLarge)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- Kostenaufstellung ---
            Text("Kostenaufstellung", style = MaterialTheme.typography.titleLarge)
            Text("Materialkosten: ${currencyFormat.format(project.materialCost)}")
            Text("Maschinenkosten: ${currencyFormat.format(project.machineCost)}")
            Text("Stromkosten: ${currencyFormat.format(project.electricityCost)}")
            Text("Arbeitskosten: ${currencyFormat.format(project.workCost)}")
            Text("Gewinn: ${currencyFormat.format(project.profit)}")

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Gesamtkosten: ${currencyFormat.format(project.totalCost)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
