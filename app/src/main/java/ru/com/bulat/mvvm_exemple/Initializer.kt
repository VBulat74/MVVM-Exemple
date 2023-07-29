package ru.com.bulat.mvvm_exemple

import kotlinx.coroutines.Dispatchers
import ru.com.bulat.foundation.SingletonScopeDependencies
import ru.com.bulat.foundation.model.coroutines.IoDispatcher
import ru.com.bulat.foundation.model.coroutines.WorkerDispatcher
import ru.com.bulat.mvvm_exemple.model.colors.InMemoryColorsRepository

object Initializer {
    fun initDependencies() {
        /**
         * Place your singleton scope dependencies here
         */
        SingletonScopeDependencies.init ({applicationContext ->
            val ioDispatcher = IoDispatcher(Dispatchers.IO)
            val workerDispatcher = WorkerDispatcher(Dispatchers.Default)
            listOf(
                ioDispatcher,
                workerDispatcher,
                InMemoryColorsRepository(ioDispatcher)
            )
        })
    }
}