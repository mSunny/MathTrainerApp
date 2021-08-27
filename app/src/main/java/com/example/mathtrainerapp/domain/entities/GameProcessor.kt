package com.example.mathtrainerapp.domain.entities

import javax.inject.Inject

class GameProcessor @Inject constructor(): RoundListener {
    var currentRound: Round? = null
    @JvmSuppressWildcards
    @Inject
    lateinit var roundCreator: (RoundTimer, Int, Int, Task, RoundListener)-> Round
    @JvmSuppressWildcards
    @Inject
    lateinit var timerCreator: (Long)-> RoundTimer
    lateinit private var gameListener: GameListener
    lateinit private var game: Game

    fun startNewGame(game: Game, gameListener: GameListener) {
        this.gameListener = gameListener
        this.game = game
        this.game.state = Game.GameState.STARTED
        this.game.currentRoundNumber = 0
        startNextRound()
    }

    fun tryAnswer(answer: String) {
        if (game.state == Game.GameState.STARTED) {
            currentRound?.tryAnswer(answer)
        }
    }

    fun pause() {
        currentRound?.pause()
    }

    fun resume() {
        currentRound?.resume()
    }

    fun stop() {
        currentRound?.cancelRound()
        finishGame()
    }

    private fun getNextRound(): Round {
        return roundCreator(
            timerCreator(game.description.timerStep),
            game.description.roundDurationInSteps,
            game.description.roundMaxScore,
            game.tasks[game.currentRoundNumber],
            this)
    }

    private fun startNextRound() {
        currentRound = getNextRound()
        currentRound!!.startRound()
        gameListener.onRoundStarted(currentRound!!.task, currentRound!!.stepsLeft * game.description.timerStep)
    }

    override fun onRoundFinished(isWon: Boolean, scoreAdded: Int) {
        gameListener.onRoundFinished(isWon, scoreAdded)
        game.score+=scoreAdded
        game.currentRoundNumber++
        if (game.currentRoundNumber < game.numberOfRounds) {
            startNextRound()
        } else {
            finishGame()
        }
    }

    override fun onStepsLeftUpdate(stepsLeft: Int) {
        gameListener.onTimeLeftUpdate(stepsLeft * game.description.timerStep)
    }

    override fun onWrongAnswer() {
        gameListener.onWrongAnswer()
    }

    private fun finishGame() {
        if (game.state != Game.GameState.FINISHED) {
            gameListener.onGameFinished(game.score)
            game.state = Game.GameState.FINISHED
        }
    }
}