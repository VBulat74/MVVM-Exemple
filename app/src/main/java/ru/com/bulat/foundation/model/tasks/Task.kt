package ru.com.bulat.foundation.model.tasks

import kotlinx.coroutines.suspendCancellableCoroutine
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.FinalResult
import ru.com.bulat.foundation.model.SuccessResult
import ru.com.bulat.foundation.model.tasks.dispatchers.Dispatcher
import ru.com.bulat.foundation.model.tasks.dispatchers.ImmediateDispatcher
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

typealias TaskListener<T> = (FinalResult<T>) -> Unit

class CancelledException(
    originException: Exception? = null
) : Exception(originException)

/**
 * Base interface for all async operations.
 */
interface Task<T> {

    /**
     * Blocking method for waiting and getting results.
     * Throws exception in case of error.
     * Task may be executed only once.
     * @throws [IllegalStateException] if task has been already executed
     * @throws [CancelledException] if task has been cancelled
     */
    fun await(): T

    /**
     * Non-blocking method for listening task results.
     * If task is cancelled before finishing, listener is not called.
     * If task is cancelled before calling this method, task is not executed.
     * Task may be executed only once.
     *
     * Listener is called via the specified dispatcher. Usually it is [MainThreadDispatcher]
     * @throws [IllegalStateException] if task has been already executed.
     */
    fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>)

    /**
     * Cancel this task and remove listener assigned by [enqueue].
     */
    fun cancel()

    suspend fun suspend() : T = suspendCancellableCoroutine {continuation ->
        enqueue(ImmediateDispatcher()){
            continuation.invokeOnCancellation { cancel() }
            when (it) {
                is SuccessResult -> continuation.resume(it.data)
                is ErrorResult -> continuation.resumeWithException(it.exception)
            }
        }

    }

}