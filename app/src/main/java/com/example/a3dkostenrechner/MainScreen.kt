package com.example.a3dkostenrechner

import android.content.res.Configuration
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.util.Locale

sealed class Screen(val route: String, val labelRes: Int, val icon: ImageVector) {
    object Calculator : Screen("calculator", R.string.nav_calculator, Icons.Default.Calculate)
    object Inventory : Screen("inventory", R.string.nav_inventory, Icons.Default.Inventory)
    object More : Screen("more", R.string.nav_more, Icons.Default.MoreHoriz)
}

val bottomNavItems = listOf(
    Screen.Calculator,
    Screen.Inventory,
    Screen.More
)

@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val statisticsViewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModelFactory(mainViewModel))
    val settings by mainViewModel.settings.collectAsState()
    val context = LocalContext.current
    
    // Activity-Referenzen sichern
    val registryOwner = LocalActivityResultRegistryOwner.current

    val locale = remember(settings.language) {
        if (settings.language == "system") {
            Locale.getDefault()
        } else {
            Locale(settings.language)
        }
    }

    val configuration = remember(locale) {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config
    }
    
    val localizedContext = remember(configuration) {
        context.createConfigurationContext(configuration)
    }

    // Wir wrappen alles in den localizedContext, stellen aber sicher,
    // dass wichtige Activity-Locals (wie die Registry) erhalten bleiben.
    CompositionLocalProvider(LocalContext provides localizedContext) {
        if (registryOwner != null) {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                MainScaffold(navController, mainViewModel, statisticsViewModel, settings)
            }
        } else {
            MainScaffold(navController, mainViewModel, statisticsViewModel, settings)
        }
    }
}

@Composable
fun MainScaffold(
    navController: androidx.navigation.NavHostController,
    mainViewModel: MainViewModel,
    statisticsViewModel: StatisticsViewModel,
    settings: CalculationSettings
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    val label = stringResource(screen.labelRes)
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val materials by mainViewModel.materials.collectAsState()
        val machines by mainViewModel.machines.collectAsState()
        val spools by mainViewModel.spools.collectAsState()
        val projects by mainViewModel.projects.collectAsState()

        NavHost(navController, startDestination = Screen.Calculator.route, Modifier.padding(innerPadding)) {
            composable(Screen.Calculator.route) {
                CostCalculatorScreen(
                    machines = machines,
                    settings = settings,
                    onSettingsChange = { mainViewModel.updateSettings(it) },
                    spools = spools,
                    onSpoolUpdate = { mainViewModel.updateSpool(it) },
                    onAddProject = { mainViewModel.addProject(it) }
                )
            }
            composable(Screen.Inventory.route) {
                SpoolManagementScreen(
                    navController = navController,
                    spools = spools,
                    onRemoveSpool = { mainViewModel.removeSpool(it) },
                    onUpdateSpool = { mainViewModel.updateSpool(it) }
                )
            }
            composable(Screen.More.route) {
                MoreScreen(navController = navController, mainViewModel = mainViewModel)
            }
            composable("projects") {
                ProjectsScreen(
                    navController = navController,
                    projects = projects,
                    onRemoveProject = { mainViewModel.removeProject(it) }
                )
            }
            composable("statistics") {
                StatisticsScreen(viewModel = statisticsViewModel)
            }
            composable("settings") {
                SettingsScreen(navController = navController, mainViewModel = mainViewModel)
            }
            composable("addSpool") {
                AddSpoolScreen(
                    navController = navController,
                    materials = materials,
                    spools = spools,
                    onAddSpool = { mainViewModel.addSpool(it) },
                    onUpdateSpool = { mainViewModel.updateSpool(it) }
                )
            }
            composable("materialManagement") {
                MaterialManagementScreen(
                    navController = navController,
                    materials = materials,
                    onAddMaterial = { mainViewModel.addMaterial(it) },
                    onRemoveMaterial = { mainViewModel.removeMaterial(it) }
                )
            }
            composable("machineManagement") {
                MachineManagementScreen(
                    navController = navController,
                    machines = machines,
                    onAddMachine = { mainViewModel.addMachine(it) },
                    onRemoveMachine = { mainViewModel.removeMachine(it) }
                )
            }
            composable("impressum") {
                ImpressumScreen(navController = navController)
            }
            composable("guide") {
                GuideScreen(navController = navController)
            }
             composable(
                route = "projectDetails/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                val project = projects.find { it.id == projectId }
                ProjectDetailsScreen(navController = navController, project = project)
            }
        }
    }
}
