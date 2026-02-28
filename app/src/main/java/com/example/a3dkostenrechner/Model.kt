package com.example.a3dkostenrechner

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class Material(val name: String, val pricePerGram: Float)

@Immutable
data class Machine(val name: String, val costPerHour: Float, val powerInWatts: Int)

@Immutable
data class Spool(
    val id: String = UUID.randomUUID().toString(),
    val materialName: String,
    val color: String,
    val manufacturer: String? = null,
    val initialWeight: Int, // in grams
    val remainingWeight: Int, // in grams
    val purchasePrice: Float
) {
    val pricePerGram: Float
        get() = if (initialWeight > 0) purchasePrice / initialWeight else 0f
}

@Immutable
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val spool: Spool?,
    val materialWeight: Float,
    val machine: Machine?,
    val printDuration: Float,
    val materialCost: Float,
    val machineCost: Float,
    val electricityCost: Float,
    val workCost: Float,
    val profit: Float,
    val totalCost: Float
)

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

@Immutable
data class CalculationSettings(
    val useInventory: Boolean = true,
    val manualPricePerGram: String = "0.03",
    val electricityRate: String = "0.30",
    val includeWorkingTime: Boolean = false,
    val hourlyRate: String = "25",
    val workingTime: String = "",
    val includeProfit: Boolean = false,
    val profitMargin: String = "",
    val appTheme: AppTheme = AppTheme.SYSTEM
)
