package ru.com.bulat.foundation.model.tasks.factories

import ru.com.bulat.foundation.model.tasks.Task

typealias TaskBody<T> = () -> T

interface TasksFactory  {

    fun <T> async (body: TaskBody<T>) : Task<T>

}