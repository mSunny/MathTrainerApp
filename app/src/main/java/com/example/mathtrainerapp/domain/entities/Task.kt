package com.example.mathtrainerapp.domain.entities

abstract class Task {
    abstract override fun toString(): String
    abstract fun checkResult(valueToCheck: String): Boolean
}