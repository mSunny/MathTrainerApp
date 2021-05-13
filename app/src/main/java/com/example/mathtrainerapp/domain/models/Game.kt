package com.example.mathtrainerapp.domain.models
interface GameListener {
    fun onTimeLeftUpdate(timeLeft: Long)
    fun onRoundFinished(isWon: Boolean, scoreAdded: Int)
    fun onWrongAnswer()
    fun onGameFinished(score: Int)
}

class Game(gameDescription: GameDescription,
           private val gameListener: GameListener,
           private val tasks: List<Task>,
           private val roundCreator: RoundCreator): RoundListener {
    private val numberOfRounds: Int = tasks.size
    private val timerStep: Long = gameDescription.timerStep
    private val roundDurationInSteps: Int =
        (gameDescription.roundDurationMs / gameDescription.timerStep).toInt()
    private val roundMaxScore: Int = gameDescription.roundMaxScore
    private var round: Round? = null
    private var gameState = GameState.BEFORE_START
    private var currentRoundNumber = 0
    private var score = 0

    enum class GameState{BEFORE_START, STARTED, FINISHED}

    fun startGame() {
        gameState = GameState.STARTED
        currentRoundNumber = 0
        startNextRound()
    }

    fun tryAnswer(answer: String) {
        if (gameState == GameState.STARTED) {
            round?.tryAnswer(answer)
        }
    }

    private fun getNextRound(): Round {
        return roundCreator.createRound(RoundTimerImplementation(timerStep),
            roundDurationInSteps,
            roundMaxScore,
            tasks[currentRoundNumber],
            this)
    }

    private fun startNextRound() {
        round = getNextRound()
        round!!.startRound()
    }

    override fun onRoundFinished(isWon: Boolean, scoreAdded: Int) {
        gameListener.onRoundFinished(isWon, scoreAdded)
        score+=scoreAdded
        currentRoundNumber++
        if (currentRoundNumber < numberOfRounds) {
            startNextRound()
        } else {
            gameListener.onGameFinished(score)
            gameState = GameState.FINISHED
        }
    }

    override fun onStepsLeftUpdate(stepsLeft: Int) {
        gameListener.onTimeLeftUpdate(stepsLeft * timerStep)
    }

    override fun onWrongAnswer() {
        gameListener.onWrongAnswer()
    }
}