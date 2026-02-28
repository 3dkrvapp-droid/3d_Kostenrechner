package com.example.a3dkostenrechner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(mainViewModel: MainViewModel) : ViewModel() {

    val totalCostOfAllProjects: StateFlow<Float> = mainViewModel.projects
        .map { projects -> projects.sumOf { it.totalCost.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

}
