package com.example.a3dkostenrechner

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataStore(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("3dkrv_data_v1", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun saveMaterials(materials: List<Material>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(materials)
        sharedPreferences.edit().putString("materials", json).apply()
    }

    suspend fun loadMaterials(): List<Material> = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString("materials", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Material>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                createDefaultMaterials()
            }
        } else {
            createDefaultMaterials()
        }
    }

    suspend fun saveMachines(machines: List<Machine>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(machines)
        sharedPreferences.edit().putString("machines", json).apply()
    }

    suspend fun loadMachines(): List<Machine> = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString("machines", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Machine>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                createDefaultMachines()
            }
        } else {
            createDefaultMachines()
        }
    }

    suspend fun saveSettings(settings: CalculationSettings) = withContext(Dispatchers.IO) {
        val json = gson.toJson(settings)
        sharedPreferences.edit().putString("settings", json).apply()
    }

    suspend fun loadSettings(): CalculationSettings = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString("settings", null)
        if (json != null) {
            try {
                gson.fromJson(json, CalculationSettings::class.java)
            } catch (e: Exception) {
                CalculationSettings()
            }
        } else {
            CalculationSettings()
        }
    }

    suspend fun saveSpools(spools: List<Spool>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(spools)
        sharedPreferences.edit().putString("spools", json).apply()
    }

    suspend fun loadSpools(): List<Spool> = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString("spools", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Spool>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    suspend fun saveProjects(projects: List<Project>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(projects)
        sharedPreferences.edit().putString("projects", json).apply()
    }

    suspend fun loadProjects(): List<Project> = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString("projects", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Project>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().clear().apply()
    }

    private fun createDefaultMaterials(): List<Material> = listOf(
        Material("PLA", 0.03f),
        Material("PETG", 0.05f)
    )

    private fun createDefaultMachines(): List<Machine> = listOf(
        Machine("Standard Drucker", 0.25f, 200)
    )
}
