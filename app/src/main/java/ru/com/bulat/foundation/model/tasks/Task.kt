package ru.com.bulat.foundation.model.tasks

import ru.com.bulat.foundation.model.FinalResult

typealias TaskListener <T> = (FinalResult<T>) -> Unit

interface Task <T> {
    fun await() : T

    /*
    * Listener called in ,ain thread
    */

    fun enqueue (listener: TaskListener<T>)

    fun cancel()
}