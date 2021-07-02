package com.example.mathtrainerapp.domain.entities

interface RoundTimer {
    fun pause()
    fun resume()
    fun startRepeating()
    fun stop()
    fun addListener(roundTimerListener: RoundTimerListener)

    interface RoundTimerListener{
        fun onTimeElapsed()
    }
}