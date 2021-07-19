package com.example.mathtrainerapp.data

import com.example.mathtrainerapp.domain.boundaries.GameRepositoryInterface
import java.util.*
import javax.inject.Inject

class GameRepository @Inject constructor(): GameRepositoryInterface {
    override fun saveGameResult(playerId: String, date: Date, score: Int) {
        //TODO: not yet implemented
    }
}