package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.domain.interactors.GameInteractor
import dagger.Component
import javax.inject.Scope

@Component(modules = [CreatorsModule::class, DispatchersModule::class])
@GameInteractorScope
interface GameInteractorComponent {
    fun injectGameInteractor(gameInteractor: GameInteractor)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameInteractorScope

