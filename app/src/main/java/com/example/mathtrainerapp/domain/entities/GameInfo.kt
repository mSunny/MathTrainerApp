package com.example.mathtrainerapp.domain.entities

import java.util.Date

class GameInfo(val player: Player) {
    val id: Int? = null
    var date: Date = Date()
    var score: Int = 0
}