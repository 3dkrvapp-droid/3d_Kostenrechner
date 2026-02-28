package com.example.a3dkostenrechner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    init {
        loadAllData()
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
