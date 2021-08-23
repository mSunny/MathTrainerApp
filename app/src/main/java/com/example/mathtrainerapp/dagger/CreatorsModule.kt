package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.domain.entities.Round
import com.example.mathtrainerapp.domain.entities.RoundListener
import com.example.mathtrainerapp.domain.entities.RoundTimer
import com.example.mathtrainerapp.domain.entities.Task
import com.example.mathtrainerapp.presentation.RoundTimerImplementation
import dagger.Module
import dagger.Provides

@Module()
class CreatorsModule {
    @Provides
    fun provideRoundCreator(): (RoundTimer, Int, Int, Task, RoundListener)-> Round {
        return ::Round
    }

    @Provides
    fun provideTimerCreator(): (Long)-> RoundTimer {
        return ::RoundTimerImplementation
    }
}