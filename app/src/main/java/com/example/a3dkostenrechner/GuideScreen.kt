package com.example.a3dkostenrechner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anleitung") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Willkommen bei 3dkrv!", style = MaterialTheme.typography.headlineSmall)
            Text("Diese App hilft dir, die Kosten deiner 3D-Drucke zu berechnen und dein Filament-Lager zu verwalten.")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Der Rechner", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Im 'Rechner'-Tab kannst du die Kosten für einen Druck kalkulieren. Du hast zwei Modi:")
            Text("1. Lager verwenden (Standard): Wähle eine deiner angelegten Spulen aus. Das verbrauchte Material wird nach der Berechnung und Speicherung als Projekt vom Restbestand der Spule abgezogen.")
            Text("2. Manuelle Eingabe: Deaktiviere 'Lager verwenden', um einen Preis pro Gramm manuell einzugeben. Das ist ideal für schnelle Kalkulationen, ohne den Lagerbestand zu beeinflussen.")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Das Lager", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Im 'Lager'-Tab siehst du alle deine Filament-Spulen. Mit dem `+`-Knopf kannst du neue Spulen anlegen. Ein Klick auf eine Spule erlaubt es dir, das verbleibende Gewicht manuell anzupassen.")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Projekte", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Wenn du im Rechner eine Berechnung im Lager-Modus durchführst, kannst du sie als Projekt speichern. Im 'Mehr' -> 'Projekte'-Menü findest du eine Liste all deiner gespeicherten Projekte und kannst dir deren Details ansehen.")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Statistiken", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Im 'Mehr' -> 'Statistiken'-Menü findest du eine visuelle Aufbereitung deiner Daten. Hier siehst du die Gesamtkosten aller Projekte, deine Ausgaben pro Monat in einem Balkendiagramm sowie deinen Gesamtverbrauch pro Materialtyp (PLA, PETG, etc.).")
        }
    }
}
