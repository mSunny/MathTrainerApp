package com.example.mathtrainerapp

import com.example.mathtrainerapp.domain.models.*
import org.junit.Assert
import org.junit.Test

class GetRandomMathTaskUnitTest {
    @Test
    fun generate_1() {
        val taskDescription = RandomMathTaskDescription (Array(1){Operation(Operator.PLUS, 1, 5)}, 1)
        val task = MathTask.getRandomMathTask(taskDescription)
        Assert.assertEquals(2, task.operands.size)
        Assert.assertEquals(1, task.operators.size)
        Assert.assertTrue(task.operands.all{operand -> operand <= 5})
        Assert.assertTrue(task.operands.all{operand -> operand >= 1})
        Assert.assertTrue(task.operators.all{operator -> operator == Operator.PLUS})
    }

    @Test
    fun generate_2() {
        val taskDescription = RandomMathTaskDescription (Array(1){Operation(Operator.PLUS, 0, 100)}, 5)
        val task = MathTask.getRandomMathTask(taskDescription)
        Assert.assertEquals(6, task.operands.size)
        Assert.assertEquals(5, task.operators.size)
        Assert.assertTrue(task.operands.all{operand -> operand <= 100})
        Assert.assertTrue(task.operands.all{operand -> operand >= 0})
        Assert.assertTrue(task.operators.all{operator -> operator == Operator.PLUS})
    }

    @Test
    fun generate_AllOperands() {
        val taskDescription = RandomMathTaskDescription (Array(4){
            Operation(Operator.PLUS, 0, 100)
            Operation(Operator.MINUS, 0, 100)
            Operation(Operator.DIVIDE, 0, 100)
            Operation(Operator.MULTIPLY, 0, 100)}, 4)
        val task = MathTask.getRandomMathTask(taskDescription)
        Assert.assertEquals(5, task.operands.size)
        Assert.assertEquals(4, task.operators.size)
        Assert.assertTrue(task.operands.all{operand -> operand <= 100})
        Assert.assertTrue(task.operands.all{operand -> operand >= 0})
        Assert.assertTrue(task.operators.all{operator -> operator in Operator.PLUS..Operator.MULTIPLY})
    }

    @Test
    fun generate_minusOrder() {
        for(i in 1..10) {
            val taskDescription =
                RandomMathTaskDescription(Array(1) { Operation(Operator.MINUS, 0, 1000) }, 20)
            val task = MathTask.getRandomMathTask(taskDescription)
            var res = task.operands.first()
            for (element in task.operands.subList(1, task.operands.size - 1)) {
                res -= element
                Assert.assertTrue(res >= 0)
            }
        }
    }

    @Test
    fun generate_divideOrder() {
        for(i in 1..10) {
            val taskDescription =
                RandomMathTaskDescription(Array(1) { Operation(Operator.DIVIDE, 0, 1000) }, 20)
            val task = MathTask.getRandomMathTask(taskDescription)
            var res = task.operands.first()
            for (element in task.operands.subList(1, task.operands.size - 1)) {
                Assert.assertTrue(res%element == 0)
                res /= element
            }
        }
    }
}