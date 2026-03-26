package com.example.a3dkostenrechner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: stringResource(R.string.more_projects)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (project != null) {
                        IconButton(onClick = {
                            scope.launch {
                                createAndSharePdf(
                                    context = context,
                                    projectName = project.name,
                                    totalCost = project.totalCost,
                                    materialCost = project.materialCost,
                                    machineCost = project.machineCost,
                                    electricityCost = project.electricityCost,
                                    workCost = project.workCost,
                                    profit = project.profit
                                )
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "PDF Export")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (project == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Text(stringResource(R.string.projects_empty))
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
            Text("${stringResource(R.string.calc_total_cost)}: ${dateFormat.format(Date(project.date))}", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- Details ---
            Text(stringResource(R.string.calc_breakdown), style = MaterialTheme.typography.titleLarge)
            project.spool?.let { Text("${stringResource(R.string.nav_inventory)}: ${it.materialName} (${it.color})", style = MaterialTheme.typography.bodyLarge) }
            Text("${stringResource(R.string.calc_weight)}: ${project.materialWeight}g", style = MaterialTheme.typography.bodyLarge)
            project.machine?.let { Text("${stringResource(R.string.calc_section_machine)}: ${it.name}", style = MaterialTheme.typography.bodyLarge) }
            Text("${stringResource(R.string.calc_duration)}: ${project.printDuration}h", style = MaterialTheme.typography.bodyLarge)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- Kostenaufstellung ---
            Text(stringResource(R.string.calc_breakdown), style = MaterialTheme.typography.titleLarge)
            Text("${stringResource(R.string.calc_mat_cost)}: ${currencyFormat.format(project.materialCost)}")
            Text("${stringResource(R.string.calc_mach_cost)}: ${currencyFormat.format(project.machineCost)}")
            Text("${stringResource(R.string.calc_elec_cost)}: ${currencyFormat.format(project.electricityCost)}")
            Text("${stringResource(R.string.calc_work_cost)}: ${currencyFormat.format(project.workCost)}")
            Text("Gewinn: ${currencyFormat.format(project.profit)}")

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "${stringResource(R.string.calc_total_cost)}: ${currencyFormat.format(project.totalCost)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
