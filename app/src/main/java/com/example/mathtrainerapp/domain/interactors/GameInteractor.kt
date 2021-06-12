package com.example.mathtrainerapp.domain.interactors

import com.example.mathtrainerapp.data.GameParameters
import com.example.mathtrainerapp.domain.boundaries.GameRepositoryInterface
import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import com.example.mathtrainerapp.domain.entities.*
import com.example.mathtrainerapp.presentation.RoundTimerImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.util.*

/*
1. start game for player
2. react on events(round finished, game finished, time left, wrong answer, error?)
3. process answers
4. process external events(stop game, pause game)
5. save new data
6. show new data
 */
class GameInteractorOnTimeLeftUpdate(timeLeft: Long): InteractorEvent()
class GameInteractorOnRoundStarted(task: Task, timeLeft: Long): InteractorEvent()
class GameInteractorOnRoundFinished(isWon: Boolean, scoreAdded: Int): InteractorEvent()
class GameInteractorOnWrongAnswer(): InteractorEvent()
class GameInteractorOnGameFinished(score: Int): InteractorEvent()

class GameInteractor(private val player: Player,
                     private val taskRepository: TaskRepositoryInterface,
                     private val gameRepository: GameRepositoryInterface): Interactor() {
    var game: Game? = null

    @ExperimentalCoroutinesApi
    override fun start(): Flow<InteractorEvent> {
        return callbackFlow<InteractorEvent> {
            val callback = object: GameListener {
                override fun onRoundStarted(task: Task, timeLeft: Long) {
                    offer(GameInteractorOnRoundStarted(task, timeLeft))
                }

                override fun onTimeLeftUpdate(timeLeft: Long) {
                    offer(GameInteractorOnTimeLeftUpdate(timeLeft))
                }

                override fun onRoundFinished(isWon: Boolean, scoreAdded: Int) {
                    offer(GameInteractorOnRoundFinished(isWon, scoreAdded))
                }

                override fun onWrongAnswer() {
                    offer(GameInteractorOnWrongAnswer())
                }

                override fun onGameFinished(score: Int) {
                    offer(GameInteractorOnGameFinished(score))
                    gameRepository.saveGameResult(player.id, Date(), score)
                    close()
                }

            }
            game = Game(
                GameParameters.gameDescription,
                callback,
                taskRepository.getTasks(GameParameters.numberOfTasks),
                ::Round,
                ::RoundTimerImplementation)
            game?.startGame()
            awaitClose { game?.stop() }
        }.catch{e -> emit(InteractorEventError(Error(ErrorType.UNKNOWN, e.toString()?: "")))}
            .flowOn(Dispatchers.IO)
    }

    fun tryAnswer(answer: String) {
        game?.tryAnswer(answer)
    }

    override fun stop() {
        game?.stop()
    }

    override fun pause() {
        game?.pause()
    }

    override fun resume() {
        game?.resume()
    }
}

