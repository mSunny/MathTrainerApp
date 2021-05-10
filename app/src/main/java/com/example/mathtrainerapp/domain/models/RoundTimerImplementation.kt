package com.example.mathtrainerapp.domain.models

import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate

class RoundTimerImplementation(private val interval: Long): RoundTimer {
    private val timer = Timer()
    private val listeners = mutableListOf<RoundTimer.RoundTimerListener>()

    override fun start() {
        timer.schedule(interval) {
            listeners.forEach{it.onTimeElapsed()}
        }
    }

    override fun startRepeating() {
        timer.scheduleAtFixedRate(0, interval) {
            listeners.forEach{it.onTimeElapsed()}
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