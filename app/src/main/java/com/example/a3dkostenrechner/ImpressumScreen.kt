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
fun ImpressumScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impressum & Lizenz") },
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
            Text("Impressum", style = MaterialTheme.typography.headlineSmall)
            Text("Angaben gemäß § 5 E-Commerce-Gesetz (ECG) sowie Offenlegung nach § 25 Mediengesetz")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Medieninhaber und Herausgeber:", fontWeight = FontWeight.Bold)
            Text("Thomas Fellner")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Wohnadresse:", fontWeight = FontWeight.Bold)
            Text("Albenedt 2\n4655 Vorchdorf\nÖsterreich")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Kontakt:", fontWeight = FontWeight.Bold)
            Text("E-Mail: 3dkrvapp@gmail.com")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tätigkeit:", fontWeight = FontWeight.Bold)
            Text("Private Entwicklung und Veröffentlichung einer Open-Source-Software (Kostenrechner-App) ohne gewerbliche Tätigkeit im Sinne der österreichischen Gewerbeordnung.")

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Lizenz", style = MaterialTheme.typography.headlineSmall)
            Text("Diese Software wird unter der GNU General Public License Version 3 (GPL v3) veröffentlicht.")
            Text("Der vollständige Lizenztext ist unter https://www.gnu.org/licenses/gpl-3.0.html einsehbar.")

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Haftungsausschluss", style = MaterialTheme.typography.headlineSmall)
            Text("Die Software wird „wie besehen“ („as is“) ohne Gewährleistung bereitgestellt. Es wird – soweit gesetzlich zulässig – keine Haftung für Schäden übernommen, die aus der Nutzung der Software entstehen.")
        }
    }
}
