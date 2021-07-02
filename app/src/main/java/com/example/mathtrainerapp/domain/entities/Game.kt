package com.example.mathtrainerapp.domain.entities

interface GameListener {
    fun onRoundStarted(task: Task, timeLeft: Long)
    fun onTimeLeftUpdate(timeLeft: Long)
    fun onRoundFinished(isWon: Boolean, scoreAdded: Int)
    fun onWrongAnswer()
    fun onGameFinished(score: Int)
}

class Game(gameDescription: GameDescription,
           private val gameListener: GameListener,
           private val tasks: List<Task>,
           private val roundCreator: (RoundTimer, Int, Int, Task, RoundListener)-> Round,
           private val timerCreator: (Long)-> RoundTimer): RoundListener {
    private val numberOfRounds: Int = tasks.size
    private val timerStep: Long = gameDescription.timerStep
    private val roundDurationInSteps: Int = gameDescription.roundDurationInSteps
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

    fun pause() {
        round?.pause()
    }

    fun resume() {
        round?.resume()
    }

    fun stop() {
        round?.cancelRound()
        finishGame()
    }

    private fun getNextRound(): Round {
        return roundCreator(
            timerCreator(timerStep),
            roundDurationInSteps,
            roundMaxScore,
            tasks[currentRoundNumber],
            this)
    }

    private fun startNextRound() {
        round = getNextRound()
        round!!.startRound()
        gameListener.onRoundStarted(round!!.task, round!!.stepsLeft * timerStep)
    }

    override fun onRoundFinished(isWon: Boolean, scoreAdded: Int) {
        gameListener.onRoundFinished(isWon, scoreAdded)
        score+=scoreAdded
        currentRoundNumber++
        if (currentRoundNumber < numberOfRounds) {
            startNextRound()
        } else {
            finishGame()
        }
    }

    override fun onStepsLeftUpdate(stepsLeft: Int) {
        gameListener.onTimeLeftUpdate(stepsLeft * timerStep)
    }

    override fun onWrongAnswer() {
        gameListener.onWrongAnswer()
    }

    private fun finishGame() {
        if (gameState != GameState.FINISHED) {
            gameListener.onGameFinished(score)
            gameState = GameState.FINISHED
        }
    }
}