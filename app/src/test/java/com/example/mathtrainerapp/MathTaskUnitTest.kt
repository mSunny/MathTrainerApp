package com.example.mathtrainerapp

import com.example.mathtrainerapp.domain.models.*
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalArgumentException

class MathTaskUnitTest {
    @Test
    fun wrongInput_1() {
        Assert.assertThrows("List of operands shouldn't be empty",
            IllegalArgumentException::class.java) {
            MathTask(listOf(), listOf(Operator.PLUS))
        }
    }

    @Test
    fun wrongInput_2() {
        Assert.assertThrows("Number of operands should be equal to number of operators+1",
            IllegalArgumentException::class.java) {
            MathTask(listOf(1, 2, 3), listOf(Operator.PLUS))
        }
    }

    @Test
    fun wrongInput_3() {
        Assert.assertThrows("Number of operands should be equal to number of operators+1",
            IllegalArgumentException::class.java) {
            MathTask(listOf(1, 2, 3), listOf(Operator.MINUS, Operator.MINUS, Operator.MINUS))
        }
    }

    @Test
    fun toString_1() {
        val task = MathTask(listOf(1, 2, 3, 4), listOf(Operator.PLUS, Operator.PLUS, Operator.PLUS))
        Assert.assertEquals("1+2+3+4", task.toString())
    }

    @Test
    fun toString_2() {
        val task = MathTask(listOf(75, 94, 185, 34, 7), listOf(Operator.PLUS, Operator.MINUS, Operator.DIVIDE, Operator.MULTIPLY))
        Assert.assertEquals("75+94-185/34*7", task.toString())
    }

    @Test
    fun toString_3() {
        val task = MathTask(listOf(75, 94, 185, 34, 7, 0, 6, 432, 10000001),
            listOf(Operator.PLUS, Operator.PLUS, Operator.MINUS, Operator.MINUS, Operator.DIVIDE, Operator.DIVIDE, Operator.MULTIPLY, Operator.MULTIPLY))
        Assert.assertEquals("75+94+185-34-7/0/6*432*10000001", task.toString())
    }

    @Test
    fun checkResult_1() {
        val task = MathTask(listOf(1, 2, 3, 4), listOf(Operator.PLUS, Operator.PLUS, Operator.PLUS))
        Assert.assertTrue(task.checkResult("10"))
        Assert.assertFalse(task.checkResult("100"))
        Assert.assertFalse(task.checkResult("AAAA"))
        Assert.assertFalse(task.checkResult(""))
    }

    @Test
    fun checkResult_2() {
        val task = MathTask(listOf(75, 94, 155, 45, 29, 5, 10, 432, 1001),
            listOf(Operator.PLUS, Operator.PLUS, Operator.MINUS, Operator.MINUS, Operator.DIVIDE, Operator.DIVIDE, Operator.MULTIPLY, Operator.MULTIPLY))
        Assert.assertTrue(task.checkResult("2162160"))
        Assert.assertFalse(task.checkResult("10000000001"))
        Assert.assertFalse(task.checkResult("BBBBBB"))
    }
}