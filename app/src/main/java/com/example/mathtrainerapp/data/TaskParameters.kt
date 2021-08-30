package com.example.mathtrainerapp.data

import com.example.mathtrainerapp.domain.entities.*

object TaskParameters {
    enum class TaskType{MATH, TEXT}
    enum class TaskSource{RANDOM, MEMORY, NETWORK}

    private val DEFAULT_TASK_TYPE = TaskType.MATH
    private val DEFAULT_TASK_SOURCE = TaskSource.RANDOM
    val DEFAULT_TASK_DESCRIPTION =
        RandomMathTaskDescription(
            arrayOf(
                Operation((Operator.MINUS), 0, 10),
                Operation((Operator.PLUS), 0, 10)
            ), 1
        )

    val TASK_DESCRIPTION = DEFAULT_TASK_DESCRIPTION
    val TASK_TYPE = DEFAULT_TASK_TYPE
    val TASK_SOURCE = DEFAULT_TASK_SOURCE
}
