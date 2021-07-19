package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.presentation.GameActivity
import dagger.Component

@Component(modules = [DataModule::class])
interface AppComponent {
    fun injectGameActivity(gameActivity: GameActivity)
}