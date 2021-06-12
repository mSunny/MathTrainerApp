package com.example.mathtrainerapp.domain.entities

interface RoundCreator {
    fun createRound(timer: RoundTimer,
                    roundDurationInSteps: Int,
                    roundMaxScore: Int,
                    task: Task,
                    listener: RoundListener): Round {
        return Round(timer, roundDurationInSteps, roundMaxScore, task, listener)
    }
}