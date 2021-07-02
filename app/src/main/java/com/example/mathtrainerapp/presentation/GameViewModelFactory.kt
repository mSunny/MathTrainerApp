package com.example.mathtrainerapp.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(private val mApplication: Application, private val param: String) :
    ViewModelProvider.Factory {
    @SuppressWarnings("unchecked")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(mApplication, param) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}