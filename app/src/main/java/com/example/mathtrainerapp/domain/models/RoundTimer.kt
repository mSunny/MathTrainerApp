package com.example.mathtrainerapp.domain.models

interface RoundTimer {
    fun start()
    fun startRepeating()
    fun stop()
    fun addListener(roundTimerListener: RoundTimerListener)

    interface RoundTimerListener{
        fun onTimeElapsed()
    }
}