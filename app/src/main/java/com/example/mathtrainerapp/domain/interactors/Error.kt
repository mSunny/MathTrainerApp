package com.example.mathtrainerapp.domain.interactors

enum class ErrorType {UNKNOWN}
class Error(val type: ErrorType, val description: String)