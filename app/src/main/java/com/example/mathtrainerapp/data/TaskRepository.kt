package com.example.mathtrainerapp.data

import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import com.example.mathtrainerapp.domain.entities.MathTask
import com.example.mathtrainerapp.domain.entities.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(): TaskRepositoryInterface {
        override fun getTasks(tasksAmount: Int): List<Task> {
            val res = mutableListOf<Task>()
            for(i in 1..tasksAmount) {
                res.add(getTask())
            }
            return res
        }

        private fun getTask(): Task {
            return when(TaskParameters.TASK_SOURCE) {
                TaskParameters.TaskSource.RANDOM -> getRandomTask(TaskParameters.TASK_TYPE)
                TaskParameters.TaskSource.MEMORY -> TODO()
                TaskParameters.TaskSource.NETWORK -> TODO()
            }
        }

        private fun getRandomTask(taskType: TaskParameters.TaskType): Task {
            return when(taskType){
                TaskParameters.TaskType.MATH -> MathTask.getRandomMathTask(TaskParameters.TASK_DESCRIPTION)
                TaskParameters.TaskType.TEXT -> TODO()
            }
        }
}