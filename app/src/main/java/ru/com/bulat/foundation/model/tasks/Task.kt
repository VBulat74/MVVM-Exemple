package ru.com.bulat.foundation.model.tasks

import ru.com.bulat.foundation.model.FinalResult
import ru.com.bulat.foundation.model.tasks.dispatchers.Dispatcher

typealias TaskListener <T> = (FinalResult<T>) -> Unit

class CancelledException(
    originException : Exception? = null
) : Exception(originException)

interface Task <T> {
    fun await() : T

    /*
    * Listener called in ,ain thread
    */

    fun enqueue (dispatcher : Dispatcher, listener: TaskListener<T>)

    fun cancel()
}