package com.example.mathtrainerapp

import com.example.mathtrainerapp.domain.entities.*
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class RoundUnitTest {

    @Test
    fun checkStart() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 2, 100, task, listener)
        round.startRound()
        verify(testRoundTimer).startRepeating()
    }

    @Test
    fun checkCancel() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 2, 100, task, listener)

        round.startRound()
        round.cancelRound()

        verify(testRoundTimer).startRepeating()
        verify(testRoundTimer).addListener(any())
    }

    @Test
    fun checkLostRound() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 2, 100, task, listener)
        var timerListener: RoundTimer.RoundTimerListener? = null
        `when`(testRoundTimer.addListener(any())).then { call ->
            val argument = call.getArgument<RoundTimer.RoundTimerListener>(0)
            argument.also { timerListener = it }
        }

        round.startRound()
        timerListener?.onTimeElapsed()
        timerListener?.onTimeElapsed()

        verify(testRoundTimer).addListener(any())
        verify(testRoundTimer).startRepeating()
        verify(testRoundTimer).stop()
        verify(listener).onRoundFinished(false, 0)
        verify(listener).onStepsLeftUpdate(1)
    }

    @Test
    fun checkWonRound() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 2, 100, task, listener)
        var timerListener: RoundTimer.RoundTimerListener? = null
        `when`(testRoundTimer.addListener(any())).then { call ->
            val argument = call.getArgument<RoundTimer.RoundTimerListener>(0)
            argument.also { timerListener = it }
        }

        round.startRound()
        timerListener?.onTimeElapsed()
        round.tryAnswer("2")

        verify(testRoundTimer).addListener(any())
        verify(testRoundTimer).startRepeating()
        verify(testRoundTimer).stop()
        verify(listener).onRoundFinished(true, 50)
        verify(listener).onStepsLeftUpdate(1)
    }

    @Test
    fun checkLostRoundWithWrongAnswer() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 2, 100, task, listener)
        var timerListener: RoundTimer.RoundTimerListener? = null
        `when`(testRoundTimer.addListener(any())).then { call ->
            val argument = call.getArgument<RoundTimer.RoundTimerListener>(0)
            argument.also { timerListener = it }
        }

        round.startRound()
        timerListener?.onTimeElapsed()
        round.tryAnswer("3")
        timerListener?.onTimeElapsed()

        verify(testRoundTimer).addListener(any())
        verify(testRoundTimer).startRepeating()
        verify(testRoundTimer).stop()
        verify(listener).onRoundFinished(false, 0)
        verify(listener).onStepsLeftUpdate(1)
    }

    @Test
    fun checkLostRoundWithLateAnswer() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 3, 100, task, listener)
        var timerListener: RoundTimer.RoundTimerListener? = null
        `when`(testRoundTimer.addListener(any())).then { call ->
            val argument = call.getArgument<RoundTimer.RoundTimerListener>(0)
            argument.also { timerListener = it }
        }

        round.startRound()
        timerListener?.onTimeElapsed()
        timerListener?.onTimeElapsed()
        timerListener?.onTimeElapsed()
        round.tryAnswer("2")

        verify(testRoundTimer).addListener(any())
        verify(testRoundTimer).startRepeating()
        verify(testRoundTimer).stop()
        verify(listener).onRoundFinished(false, 0)
        verify(listener).onStepsLeftUpdate(2)
        verify(listener).onStepsLeftUpdate(1)
    }

    @Test
    fun checkPause() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 2, 100, task, listener)
        var timerListener: RoundTimer.RoundTimerListener? = null
        `when`(testRoundTimer.addListener(any())).then { call ->
            val argument = call.getArgument<RoundTimer.RoundTimerListener>(0)
            argument.also { timerListener = it }
        }

        round.startRound()
        timerListener?.onTimeElapsed()
        round.pause()

        verify(testRoundTimer).addListener(any())
        verify(testRoundTimer).startRepeating()
        verify(testRoundTimer).pause()
        verify(listener).onStepsLeftUpdate(1)
    }

    @Test
    fun checkPauseAndResume() {
        val task = MathTask(listOf(1, 1), listOf(Operator.PLUS))
        val listener = mock(RoundListener::class.java)
        val testRoundTimer = mock(RoundTimer::class.java)
        val round = Round(testRoundTimer, 2, 100, task, listener)
        var timerListener: RoundTimer.RoundTimerListener? = null
        `when`(testRoundTimer.addListener(any())).then { call ->
            val argument = call.getArgument<RoundTimer.RoundTimerListener>(0)
            argument.also { timerListener = it }
        }

        round.startRound()
        timerListener?.onTimeElapsed()
        round.pause()
        round.resume()
        timerListener?.onTimeElapsed()

        verify(testRoundTimer).addListener(any())
        verify(testRoundTimer).startRepeating()
        verify(testRoundTimer).pause()
        verify(listener).onRoundFinished(false, 0)
        verify(listener).onStepsLeftUpdate(1)
    }
}