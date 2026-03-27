package com.example.a3dkostenrechner

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStore(application)
    private val gson = Gson()

    private val _materials = MutableStateFlow<List<Material>>(emptyList())
    val materials = _materials.asStateFlow()

    private val _machines = MutableStateFlow<List<Machine>>(emptyList())
    val machines = _machines.asStateFlow()

    private val _settings = MutableStateFlow(CalculationSettings())
    val settings = _settings.asStateFlow()

    private val _spools = MutableStateFlow<List<Spool>>(emptyList())
    val spools = _spools.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    private val _latestVersion = MutableStateFlow<String?>(null)
    val latestVersion = _latestVersion.asStateFlow()

    init {
        loadAllData()
        checkForUpdates()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _materials.value = dataStore.loadMaterials()
            _machines.value = dataStore.loadMachines()
            _settings.value = dataStore.loadSettings()
            _spools.value = dataStore.loadSpools()
            _projects.value = dataStore.loadProjects()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Hilfsfunktion für robusten Versionsvergleich
    private fun isVersionNewer(current: String, latest: String): Boolean {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLength = maxOf(currentParts.size, latestParts.size)
        
        for (i in 0 until maxLength) {
            val curr = currentParts.getOrElse(i) { 0 }
            val late = latestParts.getOrElse(i) { 0 }
            if (late > curr) return true
            if (late < curr) return false
        }
        return false
    }

    fun checkForUpdates() {
        if (!isNetworkAvailable()) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val currentVersion = getApplication<Application>().packageManager
                        .getPackageInfo(getApplication<Application>().packageName, 0).versionName ?: "1.0"
                    
                    val apiUri = "https://api.github.com/repos/3dkrvapp-droid/3d_Kostenrechner/releases/latest"
                    val response = URL(apiUri).readText()
                    val json = JsonParser.parseString(response).asJsonObject
                    val latestV = json.get("tag_name").asString.replace("v", "")
                    
                    if (isVersionNewer(currentVersion, latestV)) {
                        _latestVersion.value = latestV
                    } else {
                        _latestVersion.value = null
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    fun restoreFromBackup(jsonContent: String): Boolean {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val data: Map<String, Any> = gson.fromJson(jsonContent, type)
            
            viewModelScope.launch {
                (data["materials"] as? List<*>)?.let { list ->
                    val materialsJson = gson.toJson(list)
                    val materialList: List<Material> = gson.fromJson(materialsJson, object : TypeToken<List<Material>>() {}.type)
                    _materials.value = materialList
                    dataStore.saveMaterials(materialList)
                }
                
                (data["machines"] as? List<*>)?.let { list ->
                    val machinesJson = gson.toJson(list)
                    val machineList: List<Machine> = gson.fromJson(machinesJson, object : TypeToken<List<Machine>>() {}.type)
                    _machines.value = machineList
                    dataStore.saveMachines(machineList)
                }

                (data["spools"] as? List<*>)?.let { list ->
                    val spoolsJson = gson.toJson(list)
                    val spoolList: List<Spool> = gson.fromJson(spoolsJson, object : TypeToken<List<Spool>>() {}.type)
                    _spools.value = spoolList
                    dataStore.saveSpools(spoolList)
                }

                (data["projects"] as? List<*>)?.let { list ->
                    val projectsJson = gson.toJson(list)
                    val projectList: List<Project> = gson.fromJson(projectsJson, object : TypeToken<List<Project>>() {}.type)
                    _projects.value = projectList
                    dataStore.saveProjects(projectList)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun addMaterial(material: Material) {
        viewModelScope.launch {
            val updatedList = _materials.value + material
            _materials.value = updatedList
            dataStore.saveMaterials(updatedList)
        }
    }

    fun removeMaterial(material: Material) {
        viewModelScope.launch {
            val updatedList = _materials.value - material
            _materials.value = updatedList
            dataStore.saveMaterials(updatedList)
        }
    }

    fun addMachine(machine: Machine) {
        viewModelScope.launch {
            val updatedList = _machines.value + machine
            _machines.value = updatedList
            dataStore.saveMachines(updatedList)
        }
    }

    fun removeMachine(machine: Machine) {
        viewModelScope.launch {
            val updatedList = _machines.value - machine
            _machines.value = updatedList
            dataStore.saveMachines(updatedList)
        }
    }

    fun updateSettings(newSettings: CalculationSettings) {
        viewModelScope.launch {
             _settings.value = newSettings
             dataStore.saveSettings(newSettings)
        }
    }

    fun addSpool(spool: Spool) {
        viewModelScope.launch {
            val updatedList = _spools.value + spool
            _spools.value = updatedList
            dataStore.saveSpools(updatedList)
        }
    }

    fun removeSpool(spool: Spool) {
        viewModelScope.launch {
            val updatedList = _spools.value - spool
            _spools.value = updatedList
            dataStore.saveSpools(updatedList)
        }
    }

    fun updateSpool(spool: Spool) {
        viewModelScope.launch {
            val updatedList = _spools.value.map {
                if (it.id == spool.id) spool else it
            }
            _spools.value = updatedList
            dataStore.saveSpools(updatedList)
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            val updatedList = _projects.value + project
            _projects.value = updatedList
            dataStore.saveProjects(updatedList)
        }
    }

    fun removeProject(project: Project) {
        viewModelScope.launch {
            val updatedList = _projects.value - project
            _projects.value = updatedList
            dataStore.saveProjects(updatedList)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            dataStore.clearAllData()
            loadAllData()
        }
    }
}
