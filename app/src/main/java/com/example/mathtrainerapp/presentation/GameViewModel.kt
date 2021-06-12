package com.example.mathtrainerapp.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mathtrainerapp.domain.entities.Player

class GameViewModel(application: Application, playerId: Int): AndroidViewModel(application)  {
    lateinit var player: Player
}