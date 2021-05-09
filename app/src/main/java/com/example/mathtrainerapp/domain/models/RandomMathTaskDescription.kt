package com.example.mathtrainerapp.domain.models

class Operation(val operator: Operator,
                val minOperand: Int,
                val maxOperand: Int)

class RandomMathTaskDescription(val operationsAllowed: Array<Operation>, val numberOfOperations: Int)