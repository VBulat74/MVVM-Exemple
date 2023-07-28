package ru.com.bulat.mvvm_exemple

import android.app.Application
import kotlinx.coroutines.Dispatchers
import ru.com.bulat.foundation.BaseApplication
import ru.com.bulat.foundation.model.coroutines.IoDispatcher
import ru.com.bulat.foundation.model.coroutines.WorkerDispatcher

import ru.com.bulat.foundation.model.dispatchers.MainThreadDispatcher
import ru.com.bulat.mvvm_exemple.model.colors.InMemoryColorsRepository
import java.util.concurrent.Executors

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val workerDispatcher = WorkerDispatcher(Dispatchers.Default)

    /**
     * Place your singleton scope dependencies here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        InMemoryColorsRepository(ioDispatcher)
    )

}