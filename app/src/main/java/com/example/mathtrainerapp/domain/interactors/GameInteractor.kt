package com.example.mathtrainerapp.domain.interactors

import com.example.mathtrainerapp.dagger.IoDispatcher
import com.example.mathtrainerapp.data.GameParameters
import com.example.mathtrainerapp.domain.boundaries.GameRepositoryInterface
import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import com.example.mathtrainerapp.domain.entities.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/*
1. start game for player
2. react on events(round finished, game finished, time left, wrong answer, error?)
3. process answers
4. process external events(stop game, pause game)
5. save new data
6. show new data
 */
class GameInteractorOnTimeLeftUpdate(val timeLeft: Long): InteractorEvent()
class GameInteractorOnRoundStarted(val task: Task, val timeLeft: Long): InteractorEvent()
class GameInteractorOnRoundFinished(val isWon: Boolean, val scoreAdded: Int): InteractorEvent()
class GameInteractorOnWrongAnswer(): InteractorEvent()
class GameInteractorOnGameFinished(val score: Int): InteractorEvent()

class GameInteractor @Inject constructor (@IoDispatcher private var dispatcher: CoroutineDispatcher,
                                          private val taskRepository: TaskRepositoryInterface,
                                          private val gameRepository: GameRepositoryInterface): Interactor() {
    @Inject
    lateinit var gameProcessor: GameProcessor
    lateinit var player: Player

    fun initInteractor(player: Player): GameInteractor {
        this.player = player
        return this
    }

    @ExperimentalCoroutinesApi
    override fun start(): Flow<InteractorEvent> {
        return callbackFlow<InteractorEvent> {
            val callback = object: GameListener {
                override fun onRoundStarted(task: Task, timeLeft: Long) {
                    trySend(GameInteractorOnRoundStarted(task, timeLeft))
                }

                override fun onTimeLeftUpdate(timeLeft: Long) {
                    trySend(GameInteractorOnTimeLeftUpdate(timeLeft))
                }

                override fun onRoundFinished(isWon: Boolean, scoreAdded: Int) {
                    trySend(GameInteractorOnRoundFinished(isWon, scoreAdded))
                }

                override fun onWrongAnswer() {
                    trySend(GameInteractorOnWrongAnswer())
                }

                override fun onGameFinished(score: Int) {
                    trySend(GameInteractorOnGameFinished(score))
                    gameRepository.saveGameResult(player.id, Date(), score)
                    close()
                }

            }
            gameProcessor.startNewGame(Game(
                GameParameters.GAME_DESCRIPTION,
                taskRepository.getTasks(GameParameters.NUMBER_OF_TASKS)), callback)
            awaitClose { gameProcessor.stop() }
        }.catch{e -> emit(InteractorEventError(Error(ErrorType.UNKNOWN, e.toString())))}
            .flowOn(dispatcher)
    }

    fun tryAnswer(answer: String) {
        CoroutineScope(dispatcher).launch {
            gameProcessor.tryAnswer(answer)
        }
    }

    override fun stop() {
        CoroutineScope(dispatcher).launch {
            gameProcessor.stop()
        }
    }

    override fun pause() {
        CoroutineScope(dispatcher).launch {
            gameProcessor.pause()
        }
    }

    override fun resume() {
        CoroutineScope(dispatcher).launch {
            gameProcessor.resume()
        }
    }
}

