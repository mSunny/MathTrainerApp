package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.domain.entities.Player
import com.example.mathtrainerapp.presentation.GameViewModel
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@GameScope
@Subcomponent(modules = [DataModule::class, CreatorsModule::class, PlayerModule::class])
interface GameComponent {
    fun injectGameViewModel(gameViewModel: GameViewModel)
    @Subcomponent.Builder
    interface Builder {
        fun setPlayerModule(playerModule: PlayerModule): Builder
        fun build(): GameComponent
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope

@Module()
class PlayerModule(val player: Player) {
    @Provides
    fun providePlayer(): Player {
        return player
    }
}