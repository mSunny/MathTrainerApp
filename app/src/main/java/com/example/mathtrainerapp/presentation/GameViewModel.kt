package com.example.mathtrainerapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathtrainerapp.dagger.DaggerGameComponent
import com.example.mathtrainerapp.domain.boundaries.GameRepositoryInterface
import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import com.example.mathtrainerapp.domain.entities.Player
import com.example.mathtrainerapp.domain.entities.Task
import com.example.mathtrainerapp.domain.interactors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GameViewModel (playerId: String,
                                                private val taskRepository: TaskRepositoryInterface,
                                                private val gameRepository: GameRepositoryInterface): ViewModel() {
    enum class EventsToShow {NONE, ROUND_LOST, ROUND_WON, WRONG_ANSWER, GAME_FINISHED, ERROR}
    val timerValueFlow = MutableStateFlow(0L)
    val eventsToShowFlow: MutableSharedFlow<Pair<EventsToShow, Any?>> =
        MutableSharedFlow(0, 0)
    val scoreFlow= MutableStateFlow(0)
    val taskFlow: MutableStateFlow<Task?> = MutableStateFlow(null)
    private var player: Player
    lateinit var gameInteractor: GameInteractor
    private var isStarted = false
    private var roundScore = 0

    init {
        val gameComponent = DaggerGameComponent.create()
        gameComponent.injectGameViewModel(this)
        player = Player(playerId, "", "")
    }

    @ExperimentalCoroutinesApi
    fun startOrResume() {
        if (isStarted) {
            gameInteractor.resume()
        } else {
            isStarted = true
            roundScore = 0
            scoreFlow.value = roundScore
            viewModelScope.launch {
                gameInteractor = GameInteractor(Dispatchers.IO, taskRepository, gameRepository)
                gameInteractor.initInteractor(player).start()
                    .collect {
                        when(it) {
                            is InteractorEventError -> eventsToShowFlow.emit(EventsToShow.ERROR to it.error.description)
                            is GameInteractorOnTimeLeftUpdate -> timerValueFlow.value = it.timeLeft
                            is GameInteractorOnRoundStarted -> {
                                timerValueFlow.value = it.timeLeft
                                taskFlow.value = it.task
                            }
                            is GameInteractorOnRoundFinished -> {
                                timerValueFlow.value = 0
                                roundScore += it.scoreAdded
                                scoreFlow.value = roundScore
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
                                isStarted = false
                            }
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