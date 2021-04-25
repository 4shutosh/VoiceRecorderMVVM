package com.task.bbrassignment.utils

sealed class RecordState<out R> {

    // recorder
    object Recording : RecordState<Nothing>()
    data class Error(val exception: Exception) : RecordState<Nothing>()
    data class Done<out T>(val done: T) : RecordState<T>()

    // player
    object Playing : RecordState<Nothing>()
    object Pause : RecordState<Nothing>()
    object End : RecordState<Nothing>()
//    data class Pause<out T>(val int: T) : RecordState<T>()

}