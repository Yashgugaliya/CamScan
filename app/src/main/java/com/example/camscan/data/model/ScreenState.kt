package com.example.camscan.data.model

sealed class ScreenState<out T> {
    object Loading : ScreenState<Nothing>()
    data class Success<out T>(val data: T) : ScreenState<T>()
    data class Error(val exception: Exception) : ScreenState<Nothing>()
}