package com.example.mathtrainerapp.data

import com.example.mathtrainerapp.domain.entities.GameDescription

class GameParameters {
    companion object {
        val DEFAULT_ROUND_DURATION_IN_STEPS = 10
        val DEFAULT_TIMER_STEP_MS = 1000L
        val DEFAULT_ROUND_MAX_SCORE = 10
        val DEFAULT_NUMBER_OF_TASKS = 10
        val DEFAULT_GAME_DESCRIPTION =
            GameDescription(
                DEFAULT_ROUND_DURATION_IN_STEPS,
                DEFAULT_TIMER_STEP_MS,
                DEFAULT_ROUND_MAX_SCORE
            )

        val gameDescription
            get() = DEFAULT_GAME_DESCRIPTION
        val numberOfTasks
            get() = DEFAULT_NUMBER_OF_TASKS
    }
}