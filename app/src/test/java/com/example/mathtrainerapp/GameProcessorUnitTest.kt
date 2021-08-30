package com.example.mathtrainerapp

import com.example.mathtrainerapp.domain.entities.*
import com.example.mathtrainerapp.presentation.RoundTimerImplementation
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify


class GameProcessorUnitTest {
    private var testRoundListener: RoundListener? = null
    private val testRound: Round = Mockito.mock(Round::class.java)
    @Suppress("UNUSED_PARAMETER")
    private fun roundCreator(
        timer: RoundTimer,
        roundDurationInSteps: Int,
        roundMaxScore: Int,
        task: Task,
        listener: RoundListener
    ): Round {
        testRoundListener = listener
        return testRound
    }

    private fun timerCreator(interval: Long): RoundTimer {
        return RoundTimerImplementation(interval)
    }

    @Test
    fun game_simple_run() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val taskDescription = RandomMathTaskDescription (Array(1){Operation(Operator.PLUS, 1, 5)}, 1)
        val task = MathTask.getRandomMathTask(taskDescription)
        val tasks = List(5){task}
        val gameProcessor = GameProcessor()
        val game = Game(gameDescription, tasks)
        gameProcessor.roundCreator = ::roundCreator
        gameProcessor.timerCreator = ::timerCreator
        gameProcessor.startNewGame(game, gameListener)

        testRoundListener?.onStepsLeftUpdate(3)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)

        verify(gameListener, times(1)).onTimeLeftUpdate(any())
        verify(gameListener, times(5)).onRoundFinished(false, 0)
        verify(gameListener).onGameFinished(0)
    }

    @Test
    fun game_with_won_rounds() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val task = MathTask(listOf(1,1), listOf(Operator.PLUS))
        val tasks = List(5){task}
        val gameProcessor = GameProcessor()
        gameProcessor.roundCreator = ::roundCreator
        gameProcessor.timerCreator = ::timerCreator
        val game = Game(gameDescription, tasks)
        gameProcessor.startNewGame(game, gameListener)

        testRoundListener?.onStepsLeftUpdate(3)
        testRoundListener?.onRoundFinished(true, 10)
        testRoundListener?.onRoundFinished(true, 10)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)

        verify(gameListener, times(1)).onTimeLeftUpdate(any())
        verify(gameListener, times(2)).onRoundFinished(true, 10)
        verify(gameListener, times(3)).onRoundFinished(false, 0)
        verify(gameListener).onGameFinished(20)
    }

    @Test
    fun game_with_won_rounds_2() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val task = MathTask(listOf(1,1), listOf(Operator.PLUS))
        val tasks = List(5){task}
        val gameProcessor = GameProcessor()
        gameProcessor.roundCreator = ::roundCreator
        gameProcessor.timerCreator = ::timerCreator
        val game = Game(gameDescription, tasks)
        gameProcessor.startNewGame(game, gameListener)

        testRoundListener?.onStepsLeftUpdate(3)
        testRoundListener?.onRoundFinished(true, 0)
        testRoundListener?.onWrongAnswer()
        testRoundListener?.onRoundFinished(true, 10)
        testRoundListener?.onWrongAnswer()
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)

        verify(gameListener, times(1)).onTimeLeftUpdate(any())
        verify(gameListener, times(1)).onRoundFinished(true, 0)
        verify(gameListener, times(1)).onRoundFinished(true, 10)
        verify(gameListener, times(3)).onRoundFinished(false, 0)
        verify(gameListener, times(2)).onWrongAnswer()
        verify(gameListener).onGameFinished(10)
    }

    @Test
    fun game_with_wrong_answers() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val task = MathTask(listOf(1,1), listOf(Operator.PLUS))
        val tasks = List(5){task}
        val gameProcessor = GameProcessor()
        gameProcessor.roundCreator = ::roundCreator
        gameProcessor.timerCreator = ::timerCreator
        val game = Game(gameDescription, tasks)
        gameProcessor.startNewGame(game, gameListener)
        gameProcessor.tryAnswer("5")
        gameProcessor.tryAnswer("000")

        testRoundListener?.onStepsLeftUpdate(3)
        testRoundListener?.onRoundFinished(true, 0)
        testRoundListener?.onWrongAnswer()
        testRoundListener?.onRoundFinished(true, 10)
        testRoundListener?.onWrongAnswer()
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)

        verify(gameListener, times(1)).onTimeLeftUpdate(any())
        verify(gameListener, times(1)).onRoundFinished(true, 0)
        verify(gameListener, times(1)).onRoundFinished(true, 10)
        verify(gameListener, times(3)).onRoundFinished(false, 0)
        verify(gameListener, times(2)).onWrongAnswer()
        verify(gameListener).onGameFinished(10)
        verify(testRound).tryAnswer("5")
        verify(testRound).tryAnswer("000")
    }

    @Test
    fun game_stop_already_finished() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val taskDescription = RandomMathTaskDescription (Array(1){Operation(Operator.PLUS, 1, 5)}, 1)
        val task = MathTask.getRandomMathTask(taskDescription)
        val tasks = List(5){task}
        val gameProcessor = GameProcessor()
        gameProcessor.roundCreator = ::roundCreator
        gameProcessor.timerCreator = ::timerCreator
        val game = Game(gameDescription, tasks)
        gameProcessor.startNewGame(game, gameListener)

        testRoundListener?.onStepsLeftUpdate(3)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)

        gameProcessor.stop()

        verify(gameListener, times(1)).onTimeLeftUpdate(any())
        verify(gameListener, times(5)).onRoundFinished(false, 0)
        verify(gameListener).onGameFinished(0)
    }

    @Test
    fun game_stop() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val taskDescription = RandomMathTaskDescription (Array(1){Operation(Operator.PLUS, 1, 5)}, 1)
        val task = MathTask.getRandomMathTask(taskDescription)
        val tasks = List(5){task}
        val gameProcessor = GameProcessor()
        gameProcessor.roundCreator = ::roundCreator
        gameProcessor.timerCreator = ::timerCreator
        val game = Game(gameDescription, tasks)
        gameProcessor.startNewGame(game, gameListener)

        testRoundListener?.onStepsLeftUpdate(3)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)

        gameProcessor.stop()

        verify(gameListener, times(1)).onTimeLeftUpdate(any())
        verify(gameListener, times(3)).onRoundFinished(false, 0)
        verify(gameListener).onGameFinished(0)
    }

    @Test
    fun game_pause_resume() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val taskDescription = RandomMathTaskDescription (Array(1){Operation(Operator.PLUS, 1, 5)}, 1)
        val task = MathTask.getRandomMathTask(taskDescription)
        val tasks = List(5){task}
        val gameProcessor = GameProcessor()
        gameProcessor.roundCreator = ::roundCreator
        gameProcessor.timerCreator = ::timerCreator
        val game = Game(gameDescription, tasks)
        gameProcessor.startNewGame(game, gameListener)

        testRoundListener?.onStepsLeftUpdate(3)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)

        gameProcessor.pause()

        verify(gameListener, times(1)).onTimeLeftUpdate(any())
        verify(gameListener, times(3)).onRoundFinished(false, 0)

        gameProcessor.resume()

        testRoundListener?.onRoundFinished(false, 0)
        testRoundListener?.onRoundFinished(false, 0)
        verify(gameListener, times(5)).onRoundFinished(false, 0)
        verify(gameListener).onGameFinished(0)
    }
}