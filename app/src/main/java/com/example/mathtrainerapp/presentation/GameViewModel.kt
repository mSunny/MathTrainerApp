package com.example.mathtrainerapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathtrainerapp.domain.boundaries.GameRepositoryInterface
import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import com.example.mathtrainerapp.domain.entities.Player
import com.example.mathtrainerapp.domain.entities.Task
import com.example.mathtrainerapp.domain.interactors.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GameViewModel(playerId: String,
                    private val taskRepository: TaskRepositoryInterface,
                    private val gameRepository: GameRepositoryInterface): ViewModel() {
    enum class EventsToShow {NONE, ROUND_LOST, ROUND_WON, WRONG_ANSWER, GAME_FINISHED, ERROR}
    private var player: Player
    private lateinit var gameInteractor: GameInteractor
    val timerValueFlow = MutableStateFlow(0L)
    val eventsToShowFlow: MutableSharedFlow<Pair<EventsToShow, Any?>> =
        MutableSharedFlow(0, 0)
    val scoreFlow= MutableStateFlow(0)
    val taskFlow: MutableStateFlow<Task?> = MutableStateFlow(null)
    var isStarted = false
    var roundScore = 0

    init {
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
                gameInteractor = GameInteractor(player, taskRepository, gameRepository)
                gameInteractor.start()
                    .collect {
                        if (it is InteractorEventError) {
                            eventsToShowFlow.emit(EventsToShow.ERROR to it.error.description)
                        } else if (it is GameInteractorOnTimeLeftUpdate) {
                            timerValueFlow.value = it.timeLeft
                        } else if (it is GameInteractorOnRoundStarted) {
                            timerValueFlow.value = it.timeLeft
                            taskFlow.value = it.task
                        } else if (it is GameInteractorOnRoundFinished) {
                            timerValueFlow.value = 0
                            roundScore = roundScore + it.scoreAdded
                            scoreFlow.value = roundScore
                            if (it.isWon) {
                                eventsToShowFlow.emit(EventsToShow.ROUND_WON to null)
                            } else {
                                eventsToShowFlow.emit(EventsToShow.ROUND_LOST to null)
                            }
                        } else if (it is GameInteractorOnWrongAnswer) {
                            eventsToShowFlow.emit(EventsToShow.WRONG_ANSWER to null)
                        } else if (it is GameInteractorOnGameFinished) {
                            taskFlow.value = null
                            scoreFlow.value = it.score
                            eventsToShowFlow.emit(EventsToShow.GAME_FINISHED to it.score)
                            isStarted = false
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