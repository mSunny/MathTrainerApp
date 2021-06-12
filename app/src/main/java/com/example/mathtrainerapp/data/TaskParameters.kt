package com.example.mathtrainerapp.data

import com.example.mathtrainerapp.domain.entities.*

class TaskParameters() {
    enum class TaskType{MATH, TEXT}
    enum class TaskSource{RANDOM, MEMORY, NETWORK}

    companion object {
        private val DEFAULT_TASK_TYPE = TaskType.MATH
        private val DEFAULT_TASK_SOURCE = TaskSource.RANDOM
        val DEFAULT_TASK_DESCRIPTION =
            RandomMathTaskDescription(
                arrayOf(
                    Operation((Operator.MINUS), 0, 10),
                    Operation((Operator.PLUS), 0, 10)
                ), 1
            )

        val taskDescription get() = DEFAULT_TASK_DESCRIPTION
        val taskType get() = DEFAULT_TASK_TYPE
        val taskSource get() = DEFAULT_TASK_SOURCE
    }
}
