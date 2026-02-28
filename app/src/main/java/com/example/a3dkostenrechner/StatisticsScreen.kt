package com.example.a3dkostenrechner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val totalCost by viewModel.totalCostOfAllProjects.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Statistiken") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gesamtkosten aller Projekte", style = MaterialTheme.typography.titleMedium)
                    Text(currencyFormat.format(totalCost), style = MaterialTheme.typography.headlineMedium)
                }
            }
            // More statistics will be added here soon.
        }
    }
}
