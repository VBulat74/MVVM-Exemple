package ru.com.bulat.foundation.model.tasks.factories

import ru.com.bulat.foundation.model.tasks.AbstractTask
import ru.com.bulat.foundation.model.tasks.SynchronizedTask
import ru.com.bulat.foundation.model.tasks.Task
import ru.com.bulat.foundation.model.tasks.TaskListener


class ThreadTasksFactory : TasksFactory {
    override fun <T> async(body: TaskBody<T>): Task<T> {
        return SynchronizedTask(ThreadTask(body))
    }

    private class ThreadTask<T> (
        private val body: TaskBody<T>
    ) : AbstractTask<T>() {

        private var thread : Thread ? = null

        override fun doEnqueue(listener: TaskListener<T>) {
            thread = Thread {
                executeBody(body, listener)
            }
            thread?.start()
        }

        override fun doCancel() {
            thread?.interrupt()
        }
    }
}