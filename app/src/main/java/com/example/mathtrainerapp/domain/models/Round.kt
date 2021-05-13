package com.example.mathtrainerapp.domain.models

interface RoundListener {
    fun onRoundFinished(isWon: Boolean, scoreAdded: Int)
    fun onStepsLeftUpdate(stepsLeft: Int)
    fun onWrongAnswer()
}

class Round(private val roundTimer: RoundTimer,
            private val roundDurationInSteps: Int,
            private val roundMaxScore: Int,
            private val task: Task,
            private val roundListener: RoundListener) {
    enum class RoundState {BEFORE_START, STARTED, FINISHED}
    private var stepsLeft = 0
    private var state = RoundState.BEFORE_START

    fun startRound() {
        stepsLeft = roundDurationInSteps
        state = RoundState.STARTED
        roundTimer.addListener(object : RoundTimer.RoundTimerListener {
            override fun onTimeElapsed() {
                if (state == RoundState.STARTED) {
                    stepsLeft -= 1
                    if (stepsLeft > 0) {
                        roundListener.onStepsLeftUpdate(stepsLeft)
                    } else {
                        finishRound(false)
                    }
                }
            }})
        roundTimer.startRepeating()

    }

    fun cancelRound() {
        roundTimer.stop()
        state = RoundState.FINISHED
    }

    fun tryAnswer(answer: String) {
        if (state != RoundState.STARTED) return
        if (task.checkResult(answer)) {
            finishRound(true)
        } else {
            roundListener.onWrongAnswer()
        }
    }

    private fun getCurrentScore(): Int {
        return stepsLeft * roundMaxScore / roundDurationInSteps
    }

    private fun finishRound(isWon: Boolean) {
        roundTimer.stop()
        state = RoundState.FINISHED
        roundListener.onRoundFinished(isWon, if (isWon) getCurrentScore() else 0)
    }
}