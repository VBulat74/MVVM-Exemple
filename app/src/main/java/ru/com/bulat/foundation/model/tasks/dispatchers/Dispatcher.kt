package ru.com.bulat.foundation.model.tasks.dispatchers

interface Dispatcher {

    fun dispatch (block : () -> Unit)
}