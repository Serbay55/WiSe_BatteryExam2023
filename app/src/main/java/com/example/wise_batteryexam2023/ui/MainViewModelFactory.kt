package com.example.wise_batteryexam2023.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory (val application: Application) : ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}