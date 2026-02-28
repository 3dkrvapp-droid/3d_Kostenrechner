package com.example.a3dkostenrechner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StatisticsViewModelFactory(private val mainViewModel: MainViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(mainViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
