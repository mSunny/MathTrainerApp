package com.example.mathtrainerapp.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathtrainerapp.App
import com.example.mathtrainerapp.dagger.GameComponent
import com.example.mathtrainerapp.dagger.PlayerModule
import com.example.mathtrainerapp.domain.entities.Player
import com.example.mathtrainerapp.domain.entities.Task
import com.example.mathtrainerapp.domain.interactors.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameViewModel(playerId: String, application: Application) : AndroidViewModel(application) {
    enum class EventsToShow {NONE, ROUND_LOST, ROUND_WON, WRONG_ANSWER, GAME_FINISHED, ERROR}
    enum class GameViewSTate {GAME_STARTED, GAME_FINISHED, GAME_NOT_STARTED}

    private var gameComponent: GameComponent
    @Inject
    lateinit var gameInteractor: GameInteractor

    var player = Player(playerId, "", "")
    var gameState = GameViewSTate.GAME_NOT_STARTED
    var gameScore = 0

    val timerValueFlow = MutableStateFlow(0L)
    val eventsToShowFlow: MutableSharedFlow<Pair<EventsToShow, Any?>> =
        MutableSharedFlow(0, 0)
    val scoreFlow= MutableStateFlow(0)
    val taskFlow: MutableStateFlow<Task?> = MutableStateFlow(null)
    val testFlow: MutableStateFlow<String> = MutableStateFlow("test")

    @ExperimentalCoroutinesApi
    fun startOrResume() {
        when(gameState) {
            GameViewSTate.GAME_STARTED -> gameInteractor.resume()
            GameViewSTate.GAME_FINISHED, GameViewSTate.GAME_NOT_STARTED -> startGame()
        }
    }

    init {
        gameComponent = (application as App).createGameComponent(PlayerModule(player = player))
        gameComponent.injectGameViewModel(this)
    }

    @ExperimentalCoroutinesApi
    fun startGame() {
        gameState = GameViewSTate.GAME_STARTED
        gameScore = 0
        scoreFlow.value = gameScore
        viewModelScope.launch {
            gameInteractor.start()
                .collect {
                    testFlow.emit(player.id)
                    when(it) {
                        is InteractorEventError -> eventsToShowFlow.emit(EventsToShow.ERROR to it.error.description)
                        is GameInteractorOnTimeLeftUpdate -> timerValueFlow.value = it.timeLeft
                        is GameInteractorOnRoundStarted -> {
                            timerValueFlow.value = it.timeLeft
                            taskFlow.value = it.task
                        }
                        is GameInteractorOnRoundFinished -> {
                            timerValueFlow.value = 0
                            gameScore += it.scoreAdded
                            scoreFlow.value = gameScore
                            if (it.isWon) {
                                eventsToShowFlow.emit(EventsToShow.ROUND_WON to null)
                            } else {
                                eventsToShowFlow.emit(EventsToShow.ROUND_LOST to null)
                            }
                        }
                        is GameInteractorOnWrongAnswer -> {
                            eventsToShowFlow.emit(EventsToShow.WRONG_ANSWER to null)
                        }
                        is GameInteractorOnGameFinished -> {
                            taskFlow.value = null
                            scoreFlow.value = it.score
                            eventsToShowFlow.emit(EventsToShow.GAME_FINISHED to it.score)
                            gameState = GameViewSTate.GAME_FINISHED
                        }
                    }
                }
        }
    }

    fun pause() {
        gameInteractor.pause()
    }

    fun resume() {
        gameInteractor.resume()
    }

    fun tryAnswer(answer: String) {
        gameInteractor.tryAnswer(answer)
    }
}