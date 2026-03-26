package com.example.a3dkostenrechner

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStore(application)

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
                    
                    if (latestV != currentVersion) {
                        _latestVersion.value = latestV
                    } else {
                        _latestVersion.value = null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
            loadAllData() // Reload default data
        }
    }
}
