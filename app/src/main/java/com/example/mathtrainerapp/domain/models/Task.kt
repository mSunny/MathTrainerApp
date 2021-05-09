package com.example.mathtrainerapp.domain.models

abstract class Task {
    abstract override fun toString(): String
    abstract fun checkResult(valueToCheck: String): Boolean
}