package com.example.mathtrainerapp.domain.boundaries

import com.example.mathtrainerapp.domain.entities.Task

interface TaskRepositoryInterface {
    fun getTasks(tasksNumber: Int): List<Task>
}