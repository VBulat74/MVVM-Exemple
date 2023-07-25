package ru.com.bulat.foundation.model.tasks

import android.os.Looper
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.FinalResult
import ru.com.bulat.foundation.model.SuccessResult
import ru.com.bulat.foundation.model.tasks.dispatchers.Dispatcher
import java.lang.Exception
import android.os.Handler as Handler

private val handler = Handler(Looper.getMainLooper())

class SimpleTasksFactory : TasksFactory {
    override fun <T> async(body: TaskBody<T>): Task<T> {
        return SimpleTask(body)
    }

    class SimpleTask<T> (
        private val body: TaskBody<T>
    ) : Task<T> {

        var thread : Thread? = null
        var canceled = false

        override fun await(): T {
            return body()
        }

        override fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>) {
            thread = Thread {
                try {
                    val data = body()
                    publishResult(listener, SuccessResult(data))

                } catch (e: Exception) {
                    publishResult(listener, ErrorResult(e))
                }
            }.apply {
                start()
            }
        }

        override fun cancel() {
            canceled = true
            thread?.interrupt()
            thread = null
        }

        private fun publishResult(listener: TaskListener<T>, result:FinalResult<T>) {
            handler.post {
                if (canceled) return@post
                listener(result)
            }
        }
    }
}