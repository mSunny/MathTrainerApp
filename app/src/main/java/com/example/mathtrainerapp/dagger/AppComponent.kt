package com.example.mathtrainerapp.dagger

import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataModule::class, DispatchersModule::class])
@Singleton
interface AppComponent {
    val gameComponentBuilder: GameComponent.Builder
}

