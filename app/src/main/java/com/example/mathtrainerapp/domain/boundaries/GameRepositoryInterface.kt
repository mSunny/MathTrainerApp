package com.example.mathtrainerapp.domain.boundaries

import com.example.mathtrainerapp.domain.entities.Player
import java.util.*

interface GameRepositoryInterface {
    fun saveGameResult(playerId: String, date: Date, score: Int)
}