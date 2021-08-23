package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.presentation.GameViewModel
import dagger.Component
import javax.inject.Scope

@GameScope
@Component(modules = [DataModule::class, DispatchersModule::class])
interface GameComponent {
    fun injectGameViewModel(gameViewModel: GameViewModel)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope