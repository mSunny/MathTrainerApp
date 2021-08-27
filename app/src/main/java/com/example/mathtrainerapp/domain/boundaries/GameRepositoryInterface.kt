package com.example.mathtrainerapp.domain.boundaries

import java.util.Date


interface GameRepositoryInterface {
    fun saveGameResult(playerId: String, date: Date, score: Int)
}