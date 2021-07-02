package com.example.mathtrainerapp.data

import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import com.example.mathtrainerapp.domain.entities.MathTask
import com.example.mathtrainerapp.domain.entities.Task

class TaskRepository: TaskRepositoryInterface {
        override fun getTasks(tasksNumber: Int): List<Task> {
            val res = mutableListOf<Task>()
            for(i in 1..tasksNumber) {
                res.add(getTask())
            }
            return res
        }

        private fun getTask(): Task {
            return when(TaskParameters.taskSource) {
                TaskParameters.TaskSource.RANDOM -> getRandomTask(TaskParameters.taskType)
                TaskParameters.TaskSource.MEMORY -> TODO()
                TaskParameters.TaskSource.NETWORK -> TODO()
            }
        }

        private fun getRandomTask(taskType: TaskParameters.TaskType): Task {
            return when(taskType){
                TaskParameters.TaskType.MATH -> MathTask.getRandomMathTask(TaskParameters.taskDescription)
                TaskParameters.TaskType.TEXT -> TODO()
            }
        }
}