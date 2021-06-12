package com.example.mathtrainerapp.presentation

import com.example.mathtrainerapp.domain.entities.RoundTimer
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class RoundTimerImplementation(private val interval: Long): RoundTimer {
    private val timer = Timer()
    private val listeners = mutableListOf<RoundTimer.RoundTimerListener>()
    private var isPaused = false

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    override fun startRepeating() {
        timer.scheduleAtFixedRate(0, interval) {
            if(!isPaused) {
                listeners.forEach { it.onTimeElapsed() }
            }
        }
    }

    override fun stop() {
        timer.cancel()
        timer.purge()
    }

    override fun addListener(roundTimerListener: RoundTimer.RoundTimerListener) {
        listeners.add(roundTimerListener)
    }
}