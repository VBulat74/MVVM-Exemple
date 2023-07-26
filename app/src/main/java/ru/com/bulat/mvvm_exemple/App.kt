package ru.com.bulat.mvvm_exemple

import android.app.Application
import ru.com.bulat.foundation.BaseApplication
import ru.com.bulat.foundation.model.tasks.factories.ThreadTasksFactory
import ru.com.bulat.foundation.model.tasks.ThreadUtils
import ru.com.bulat.foundation.model.tasks.dispatchers.MainThreadDispatcher
import ru.com.bulat.foundation.model.tasks.factories.ExecutorServiceTasksFactory
import ru.com.bulat.foundation.model.tasks.factories.HandlerThreadTaskFactory
import ru.com.bulat.mvvm_exemple.model.colors.InMemoryColorsRepository
import java.util.concurrent.Executors

class App : Application() , BaseApplication {

    //private val tasksFactory = ThreadTasksFactory()

    //private val tasksFactory = ExecutorServiceTasksFactory(Executors.newCachedThreadPool())

    private val singleThreadExecutorTasksFactory = ExecutorServiceTasksFactory(Executors.newSingleThreadExecutor())
    private val cashedThreadExecutorTasksFactory = ExecutorServiceTasksFactory(Executors.newCachedThreadPool())

    private val handlerThreadTaskFactory = HandlerThreadTaskFactory()

    private val threadUtils = ThreadUtils.Default()
    private val dispatcher = MainThreadDispatcher()

    /**
     * Place your repositories here, now we have only 1 repository
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        cashedThreadExecutorTasksFactory,
        dispatcher,
        InMemoryColorsRepository(handlerThreadTaskFactory, threadUtils)
    )
}