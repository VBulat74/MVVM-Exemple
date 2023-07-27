package ru.com.bulat.foundation.model.tasks.dispatchers

class ImmediateDispatcher : Dispatcher {
    override fun dispatch(block: () -> Unit) {
        block()
    }
}