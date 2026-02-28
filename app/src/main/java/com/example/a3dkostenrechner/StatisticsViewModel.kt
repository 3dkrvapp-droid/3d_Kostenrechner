package com.example.a3dkostenrechner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class StatisticsViewModel(mainViewModel: MainViewModel) : ViewModel() {

    val totalCostOfAllProjects: StateFlow<Float> = mainViewModel.projects
        .map { projects -> projects.sumOf { it.totalCost.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val monthlyCosts: StateFlow<Map<Int, Float>> = mainViewModel.projects
        .map { projects ->
            val calendar = Calendar.getInstance()
            val monthlyTotals = mutableMapOf<Int, Float>()

            for (project in projects) {
                calendar.timeInMillis = project.date
                val month = calendar.get(Calendar.MONTH) // 0 = Jan, 11 = Dec
                monthlyTotals[month] = (monthlyTotals[month] ?: 0f) + project.totalCost
            }
            (0..11).forEach { month ->
                if (!monthlyTotals.containsKey(month)) {
                    monthlyTotals[month] = 0f
                }
            }
            monthlyTotals.toSortedMap()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val materialConsumption: StateFlow<Map<String, Float>> = mainViewModel.projects
        .map { projects ->
            projects
                .filter { it.spool != null }
                .groupBy { it.spool!!.materialName }
                .mapValues { entry ->
                    entry.value.sumOf { it.materialWeight.toDouble() }.toFloat()
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
