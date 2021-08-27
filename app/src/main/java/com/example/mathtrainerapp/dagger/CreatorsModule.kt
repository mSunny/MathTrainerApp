package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.domain.entities.*
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