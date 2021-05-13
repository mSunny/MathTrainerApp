package com.example.mathtrainerapp

import com.example.mathtrainerapp.domain.models.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify


class GameUnitTest {

    @Test
    fun game_simple_run() {
        val gameDescription = GameDescription(100, 10, 10)
        val gameListener = Mockito.mock(GameListener::class.java)
        val roundCreator = Mockito.mock(RoundCreator::class.java)
        var testRoundListener: RoundListener? = null
        val testRound = Mockito.mock(Round::class.java)
        val taskDescription = RandomMathTaskDescription (Array(1){Operation(Operator.PLUS, 1, 5)}, 1)
        val task = MathTask.getRandomMathTask(taskDescription)
        val tasks = List(5){task}
        val game = Game(gameDescription, gameListener, tasks, roundCreator)
        Mockito.`when`(roundCreator.createRound(any(),any(),any(),any(),any())).then { call ->
                testRoundListener = call.getArgument<RoundListener>(4)
                testRound
        }

        game.startGame()

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
        val roundCreator = Mockito.mock(RoundCreator::class.java)
        var testRoundListener: RoundListener? = null
        val testRound = Mockito.mock(Round::class.java)
        val task = MathTask(listOf(1,1), listOf(Operator.PLUS))
        val tasks = List(5){task}
        val game = Game(gameDescription, gameListener, tasks, roundCreator)
        Mockito.`when`(roundCreator.createRound(any(),any(),any(),any(),any())).then { call ->
            testRoundListener = call.getArgument<RoundListener>(4)
            testRound
        }

        game.startGame()

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
        val roundCreator = Mockito.mock(RoundCreator::class.java)
        var testRoundListener: RoundListener? = null
        val testRound = Mockito.mock(Round::class.java)
        val task = MathTask(listOf(1,1), listOf(Operator.PLUS))
        val tasks = List(5){task}
        val game = Game(gameDescription, gameListener, tasks, roundCreator)
        Mockito.`when`(roundCreator.createRound(any(),any(),any(),any(),any())).then { call ->
            testRoundListener = call.getArgument<RoundListener>(4)
            testRound
        }

        game.startGame()

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
        val roundCreator = Mockito.mock(RoundCreator::class.java)
        var testRoundListener: RoundListener? = null
        val testRound = Mockito.mock(Round::class.java)
        val task = MathTask(listOf(1,1), listOf(Operator.PLUS))
        val tasks = List(5){task}
        val game = Game(gameDescription, gameListener, tasks, roundCreator)
        Mockito.`when`(roundCreator.createRound(any(),any(),any(),any(),any())).then { call ->
            testRoundListener = call.getArgument<RoundListener>(4)
            testRound
        }

        game.startGame()
        game.tryAnswer("5")
        game.tryAnswer("000")

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
}