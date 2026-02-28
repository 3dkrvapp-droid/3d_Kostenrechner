package com.example.a3dkostenrechner

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Calculator : Screen("calculator", "Rechner", Icons.Default.Calculate)
    object Inventory : Screen("inventory", "Lager", Icons.Default.Inventory)
    object More : Screen("more", "Mehr", Icons.Default.MoreHoriz)
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
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
        val settings by mainViewModel.settings.collectAsState()
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
                MoreScreen(navController = navController)
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
                SettingsScreen(navController = navController)
            }
            composable("addSpool") {
                AddSpoolScreen(
                    navController = navController,
                    materials = materials,
                    onAddSpool = { mainViewModel.addSpool(it) }
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
