package ru.com.bulat.foundation.model.tasks

import ru.com.bulat.foundation.model.Repository

typealias TaskBody<T> = () -> T

interface TasksFactory : Repository {

    fun <T> async (body: TaskBody<T>) : Task<T>

}