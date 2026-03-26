package com.example.a3dkostenrechner

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Keep
@Immutable
data class Material(
    @SerializedName("name") val name: String,
    @SerializedName("pricePerGram") val pricePerGram: Float
)

@Keep
@Immutable
data class Machine(
    @SerializedName("name") val name: String,
    @SerializedName("costPerHour") val costPerHour: Float,
    @SerializedName("powerInWatts") val powerInWatts: Int
)

@Keep
@Immutable
data class Spool(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("materialName") val materialName: String,
    @SerializedName("color") val color: String,
    @SerializedName("manufacturer") val manufacturer: String? = null,
    @SerializedName("initialWeight") val initialWeight: Int, // in grams
    @SerializedName("remainingWeight") var remainingWeight: Int, // in grams
    @SerializedName("purchasePrice") val purchasePrice: Float
) {
    val pricePerGram: Float
        get() = if (initialWeight > 0) purchasePrice / initialWeight else 0f
}

@Keep
@Immutable
data class Project(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("name") val name: String,
    @SerializedName("date") val date: Long = System.currentTimeMillis(),
    @SerializedName("spool") val spool: Spool?,
    @SerializedName("materialWeight") val materialWeight: Float,
    @SerializedName("machine") val machine: Machine?,
    @SerializedName("printDuration") val printDuration: Float,
    @SerializedName("materialCost") val materialCost: Float,
    @SerializedName("machineCost") val machineCost: Float,
    @SerializedName("electricityCost") val electricityCost: Float,
    @SerializedName("workCost") val workCost: Float,
    @SerializedName("profit") val profit: Float,
    @SerializedName("totalCost") val totalCost: Float
)

@Keep
@Immutable
data class CalculationSettings(
    @SerializedName("useInventory") val useInventory: Boolean = true,
    @SerializedName("manualPricePerGram") val manualPricePerGram: String = "0.03",
    @SerializedName("electricityRate") val electricityRate: String = "0.30",
    @SerializedName("includeWorkingTime") val includeWorkingTime: Boolean = false,
    @SerializedName("hourlyRate") val hourlyRate: String = "25",
    @SerializedName("workingTime") val workingTime: String = "",
    @SerializedName("includeProfit") val includeProfit: Boolean = false,
    @SerializedName("profitMargin") val profitMargin: String = "",
    @SerializedName("language") val language: String = "system" // "system", "de", "en"
)
