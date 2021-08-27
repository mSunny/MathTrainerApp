package com.example.mathtrainerapp.domain.entities

interface GameListener {
    fun onRoundStarted(task: Task, timeLeft: Long)
    fun onTimeLeftUpdate(timeLeft: Long)
    fun onRoundFinished(isWon: Boolean, scoreAdded: Int)
    fun onWrongAnswer()
    fun onGameFinished(score: Int)
}

class Game(val description: GameDescription,
           val tasks: List<Task>) {
    val numberOfRounds: Int = tasks.size
    var state = GameState.BEFORE_START
    var currentRoundNumber = 0
    var score = 0

    enum class GameState {BEFORE_START, STARTED, FINISHED}
}