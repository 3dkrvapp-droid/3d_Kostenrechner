package com.example.a3dkostenrechner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val totalCost by viewModel.totalCostOfAllProjects.collectAsState()
    val monthlyCosts by viewModel.monthlyCosts.collectAsState()
    val materialConsumption by viewModel.materialConsumption.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance() }

    val months = remember { listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Statistiken") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gesamtkosten aller Projekte", style = MaterialTheme.typography.titleMedium)
                    Text(currencyFormat.format(totalCost), style = MaterialTheme.typography.headlineMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Kosten pro Monat", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (monthlyCosts.values.any { it > 0f }) {
                SimpleBarChart(monthlyCosts = monthlyCosts, months = months)
            } else {
                Text("Noch keine Projekte für eine Monats-Statistik vorhanden.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Materialverbrauch", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (materialConsumption.isEmpty()) {
                        Text("Noch keine Daten vorhanden.")
                    } else {
                        materialConsumption.entries.forEach { (name, weight) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name)
                                Text("${weight.toInt()}g")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(monthlyCosts: Map<Int, Float>, months: List<String>) {
    val maxCost = monthlyCosts.values.maxOrNull() ?: 1f

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            monthlyCosts.entries.sortedBy { it.key }.forEach { (month, cost) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .height((150 * (cost / maxCost)).dp.coerceAtLeast(1.dp))
                            .width(20.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Text(months[month])
                }
            }
        }
    }
}
