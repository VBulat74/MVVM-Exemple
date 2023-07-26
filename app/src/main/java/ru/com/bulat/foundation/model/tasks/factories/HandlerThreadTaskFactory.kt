package ru.com.bulat.foundation.model.tasks.factories

import android.os.Handler
import android.os.HandlerThread
import ru.com.bulat.foundation.model.tasks.AbstractTask
import ru.com.bulat.foundation.model.tasks.SynchronizedTask
import ru.com.bulat.foundation.model.tasks.Task
import ru.com.bulat.foundation.model.tasks.TaskListener

class HandlerThreadTaskFactory : TasksFactory {

    private val thread = HandlerThread(javaClass.simpleName)

    init {
        thread.start()
    }

    private val handler = Handler(thread.looper)

    override fun <T> async(body: TaskBody<T>): Task<T> {
        return SynchronizedTask(HandlerThreadTask(body))
    }

    private inner class HandlerThreadTask<T> (
        private val body : TaskBody<T>
    ) : AbstractTask<T> () {

        private var thread : Thread? = null

        override fun doEnqueue(listener: TaskListener<T>) {
            val runnable = Runnable {
                thread = Thread {
                    executeBody(body, listener)
                }
                thread?.start()
            }
            handler.post (runnable)
        }

        override fun doCancel() {
            thread?.interrupt()
        }

    }
}