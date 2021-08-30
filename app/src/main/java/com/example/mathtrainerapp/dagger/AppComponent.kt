package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.presentation.GameActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataModule::class, DispatchersModule::class])
@Singleton
interface AppComponent {
    fun injectGameActivity(gameActivity: GameActivity)
}

