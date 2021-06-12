package com.example.mathtrainerapp.domain.interactors

import kotlinx.coroutines.flow.Flow

abstract class InteractorEvent
class InteractorEventError(val error: Error): InteractorEvent()

abstract class Interactor() {
    abstract fun start(): Flow<InteractorEvent>
    abstract fun stop()
    abstract fun pause()
    abstract fun resume()
}