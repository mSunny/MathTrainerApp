package com.example.mathtrainerapp.data

import com.example.mathtrainerapp.domain.entities.GameDescription

object GameParameters {
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

    val GAME_DESCRIPTION = DEFAULT_GAME_DESCRIPTION
    val NUMBER_OF_TASKS = DEFAULT_NUMBER_OF_TASKS
}