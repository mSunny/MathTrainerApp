
package com.example.mathtrainerapp.domain.entities

import java.lang.IllegalArgumentException
import kotlin.random.Random

enum class Operator { PLUS, MINUS, DIVIDE, MULTIPLY}

class MathTask(val operands: List<Int>, val operators: List<Operator>): Task() {

    init {
        if( operands.isEmpty()) {
            throw IllegalArgumentException("List of operands shouldn't be empty")
        }
        if (operands.size - operators.size != 1) {
            throw IllegalArgumentException("Number of operands should be equal to number of operators+1")
        }
    }

    override fun toString(): String {
        var res = ""
        res += operands[0]
        for(index in operators.indices) {
            val next = operands[index + 1]
            res = when(operators[index]){
                Operator.PLUS ->
                    "$res+"
                Operator.MINUS ->
                    "$res-"
                Operator.DIVIDE ->
                    "$res/"
                Operator.MULTIPLY ->
                    "$res*"
            }
            res += next
        }
        return res
    }

    override fun checkResult(valueToCheck: String): Boolean {
        return valueToCheck.toIntOrNull()?.let { checkResult(it) }?:false
    }

    private fun checkResult(valueToTest: Int): Boolean {
        return valueToTest == getResult()
    }

    private fun getResult(): Int {
        return calculate(operands, operators)
    }

    companion object {
        fun getRandomMathTask(taskDescription: RandomMathTaskDescription): MathTask {
            val operands = mutableListOf<Int>()
            val operators = mutableListOf<Operator>()
            var nextOperand: Int? = null
            for (nOperation in 1..taskDescription.numberOfOperations) {
                val operation = getRandomOperation(taskDescription.operationsAllowed)
                if (nextOperand == null) {
                    nextOperand = getOperand(operation.minOperand, operation.maxOperand, operation.operator)
                    operands.add(nextOperand)
                }
                val currentResult = calculate(operands, operators)
                operators.add(operation.operator)
                nextOperand = getOperand(operation.minOperand, operation.maxOperand, operation.operator, currentResult)
                operands.add(nextOperand)
            }
            return MathTask(operands, operators)
        }

        private fun calculate(operands: List<Int>, operators: List<Operator>): Int {
            var res = operands[0]
            for (index in operators.indices) {
                val next = operands[index + 1]
                res = when (operators[index]) {
                    Operator.PLUS ->
                        res + next
                    Operator.MINUS ->
                        res - next
                    Operator.DIVIDE ->
                        res / next
                    Operator.MULTIPLY ->
                        res * next
                }
            }
            return res
        }

        private fun getOperand(minOperand: Int, maxOperand: Int) = Random.nextInt(minOperand, maxOperand + 1)

        private fun getOperand(minOperand: Int,
                               maxOperand: Int,
                               operator: Operator,
                               firstOperand: Int? = null) : Int {
            return if(firstOperand == null) {
                getOperand(minOperand, maxOperand)
            } else when(operator) {
                Operator.PLUS -> getOperand(minOperand, maxOperand)
                Operator.MULTIPLY -> getOperand(minOperand, maxOperand)
                Operator.MINUS -> getOperand(minOperand, firstOperand)
                Operator.DIVIDE -> {
                    firstOperand.getRandomDivider(minOperand, maxOperand)
                }
            }
        }

        private fun getRandomOperation(operationsAllowed: Array<Operation>): Operation {
            return operationsAllowed.random()
        }

        private fun Int.getRandomDivider(minValue: Int, maxValue: Int): Int {
            return this.getAllDividers()
                .filter { it in minValue..maxValue }
                .random()
        }

        private fun Int.getAllDividers(): List<Int> {
            return(1..this).toList().filter { element -> this % element == 0 }
        }
    }
}
