package com.example.mathtrainerapp.dagger

import com.example.mathtrainerapp.data.GameRepository
import com.example.mathtrainerapp.data.TaskRepository
import com.example.mathtrainerapp.domain.boundaries.GameRepositoryInterface
import com.example.mathtrainerapp.domain.boundaries.TaskRepositoryInterface
import dagger.Binds
import dagger.Module

@Module(includes = [AppBindModule::class])
class DataModule {
}

@Module
interface AppBindModule{
    @Binds
    fun bindTaskRepositoryInterfaceToTaskRepository(taskRepository: TaskRepository):
            TaskRepositoryInterface

    @Binds
    fun bindGameRepositoryInterfaceToGameRepository(gameRepository: GameRepository):
            GameRepositoryInterface
}