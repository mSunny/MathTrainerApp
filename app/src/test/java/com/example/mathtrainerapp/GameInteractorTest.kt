package com.example.mathtrainerapp

import com.example.mathtrainerapp.data.GameParameters
import com.example.mathtrainerapp.domain.boundaries.GameRepositoryInterface
import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import com.example.mathtrainerapp.domain.entities.*
import com.example.mathtrainerapp.domain.interactors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.ArgumentCaptor




class GameInteractorTest {
    @ExperimentalCoroutinesApi
    private val coroutineDispatcher = TestCoroutineDispatcher()

    private fun roundCreator(
        timer: RoundTimer,
        roundDurationInSteps: Int,
        roundMaxScore: Int,
        task: Task,
        listener: RoundListener
    ): Round = Round(timer, roundDurationInSteps, roundMaxScore, task, listener)

    private fun timerCreator(interval: Long): RoundTimer {
        return RoundTimerImplementation(interval)
    }

    private val gameProcessor = GameProcessor()


    @ExperimentalCoroutinesApi
    @Test
    fun start() {
        runBlocking {
            val gameRepository = Mockito.mock(GameRepositoryInterface::class.java)
            val taskRepository = Mockito.mock(TaskRepositoryInterface::class.java)
            val player = Player("123", "", "")
            val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
            val tasks = List(5) { task }
            val interactor =
                GameInteractor(coroutineDispatcher, taskRepository, gameRepository, player)
            gameProcessor.roundCreator = ::roundCreator
            gameProcessor.timerCreator = ::timerCreator
            interactor.gameProcessor = gameProcessor

            Mockito.`when`(taskRepository.getTasks(any())).thenReturn(tasks)
            val res = mutableListOf<InteractorEvent>()

            interactor.start().toList(res)
            verify(gameRepository).saveGameResult(eq("123"), any(), eq(0))

            Assert.assertEquals(5, res.filterIsInstance<GameInteractorOnRoundFinished>().count())
            Assert.assertEquals(1, res.filterIsInstance<GameInteractorOnGameFinished>().count())
            Assert.assertEquals(0, res.filterIsInstance<InteractorEventError>().count())
            Assert.assertEquals(5, res.filterIsInstance<GameInteractorOnRoundStarted>().count())
            Assert.assertEquals(0, res.filterIsInstance<GameInteractorOnWrongAnswer>().count())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun start2() {
        runBlocking {
            val gameRepository = Mockito.mock(GameRepositoryInterface::class.java)
            val taskRepository = Mockito.mock(TaskRepositoryInterface::class.java)
            val player = Player("123", "", "")
            val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
            val tasks = List(5) { task }
            val interactor = GameInteractor(coroutineDispatcher, taskRepository, gameRepository, player)
            gameProcessor.roundCreator = ::roundCreator
            gameProcessor.timerCreator = ::timerCreator
            interactor.gameProcessor = gameProcessor
            Mockito.`when`(taskRepository.getTasks(any())).thenReturn(tasks)
            val flow = interactor.start()
            val res = mutableListOf<InteractorEvent>()
            flow.collect {
                res.add(it)
                if (it is GameInteractorOnRoundStarted) {
                    interactor.tryAnswer("3")
                    interactor.tryAnswer("2")
                }
                if (it is InteractorEventError) {
                    println(it.toString())
                }
            }

            verify(taskRepository).getTasks(GameParameters.NUMBER_OF_TASKS)
            verify(gameRepository).saveGameResult(eq("123"), any(), any())

            Assert.assertEquals(5, res.filterIsInstance<GameInteractorOnRoundFinished>().count())
            Assert.assertEquals(1, res.filterIsInstance<GameInteractorOnGameFinished>().count())
            Assert.assertEquals(0, res.filterIsInstance<InteractorEventError>().count())
            Assert.assertEquals(5, res.filterIsInstance<GameInteractorOnRoundStarted>().count())
            Assert.assertEquals(5, res.filterIsInstance<GameInteractorOnWrongAnswer>().count())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun stop() {
        runBlocking {
            val gameRepository = Mockito.mock(GameRepositoryInterface::class.java)
            val taskRepository = Mockito.mock(TaskRepositoryInterface::class.java)
            val player = Player("123", "", "")
            val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
            val tasks = List(5) { task }
            val interactor = GameInteractor(Dispatchers.IO, taskRepository, gameRepository, player)
            gameProcessor.roundCreator = ::roundCreator
            gameProcessor.timerCreator = ::timerCreator
            interactor.gameProcessor = gameProcessor
            Mockito.`when`(taskRepository.getTasks(any())).thenReturn(tasks)
            val flow = interactor.start()
            val res = mutableListOf<InteractorEvent>()
            flow.collect {
                res.add(it)
                if (it is GameInteractorOnRoundFinished) {
                    interactor.stop()
                }
            }

            verify(taskRepository).getTasks(GameParameters.NUMBER_OF_TASKS)
            verify(gameRepository).saveGameResult(eq("123"), any(), any())

            Assert.assertEquals(1, res.filterIsInstance<GameInteractorOnRoundFinished>().count())
            Assert.assertEquals(1, res.filterIsInstance<GameInteractorOnGameFinished>().count())
            Assert.assertEquals(0, res.filterIsInstance<InteractorEventError>().count())
            Assert.assertEquals(2, res.filterIsInstance<GameInteractorOnRoundStarted>().count())
            Assert.assertEquals(0, res.filterIsInstance<GameInteractorOnWrongAnswer>().count())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun pause_resume() {
        runBlocking {
            val gameRepository = Mockito.mock(GameRepositoryInterface::class.java)
            val taskRepository = Mockito.mock(TaskRepositoryInterface::class.java)
            val player = Player("123", "", "")
            val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
            val tasks = List(5) { task }
            val interactor = GameInteractor(Dispatchers.IO, taskRepository, gameRepository, player)
            gameProcessor.roundCreator = ::roundCreator
            gameProcessor.timerCreator = ::timerCreator
            interactor.gameProcessor = gameProcessor
            Mockito.`when`(taskRepository.getTasks(any())).thenReturn(tasks)
            val flow = interactor.start()
            val res = mutableListOf<InteractorEvent>()
            var count = 0
            flow.collect {
                res.add(it)
                if (it is GameInteractorOnRoundFinished && count == 0) {
                    interactor.pause()
                    Assert.assertEquals(1, res.filterIsInstance<GameInteractorOnRoundFinished>().count())
                    Assert.assertEquals(0, res.filterIsInstance<GameInteractorOnGameFinished>().count())
                    Assert.assertEquals(0, res.filterIsInstance<InteractorEventError>().count())
                    Assert.assertEquals(1, res.filterIsInstance<GameInteractorOnRoundStarted>().count())
                    Assert.assertEquals(0, res.filterIsInstance<GameInteractorOnWrongAnswer>().count())
                    interactor.resume()
                }
                count++
            }

            verify(taskRepository).getTasks(GameParameters.NUMBER_OF_TASKS)
            verify(gameRepository).saveGameResult(eq("123"), any(), any())

            Assert.assertEquals(5, res.filterIsInstance<GameInteractorOnRoundFinished>().count())
            Assert.assertEquals(1, res.filterIsInstance<GameInteractorOnGameFinished>().count())
            Assert.assertEquals(0, res.filterIsInstance<InteractorEventError>().count())
            Assert.assertEquals(5, res.filterIsInstance<GameInteractorOnRoundStarted>().count())
            Assert.assertEquals(0, res.filterIsInstance<GameInteractorOnWrongAnswer>().count())
        }
    }
}