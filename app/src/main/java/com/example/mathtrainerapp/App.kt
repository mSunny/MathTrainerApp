package com.example.mathtrainerapp

import android.app.Application
import android.content.Context
import com.example.mathtrainerapp.dagger.AppComponent
import com.example.mathtrainerapp.dagger.DaggerAppComponent
import com.example.mathtrainerapp.dagger.GameComponent
import com.example.mathtrainerapp.dagger.PlayerModule
import com.github.anrwatchdog.ANRWatchDog

class App: Application() {
    lateinit var appComponent: AppComponent
        private set
    lateinit var gameComponent: GameComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        ANRWatchDog().start()
    }

    fun createGameComponent(playerModule: PlayerModule): GameComponent {
        gameComponent = appComponent.gameComponentBuilder.setPlayerModule(playerModule).build()
        return gameComponent
    }


}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> applicationContext.appComponent
    }