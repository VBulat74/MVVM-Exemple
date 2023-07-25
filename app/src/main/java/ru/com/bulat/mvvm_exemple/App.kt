package ru.com.bulat.mvvm_exemple

import android.app.Application
import ru.com.bulat.foundation.BaseApplication
import ru.com.bulat.foundation.model.Repository
import ru.com.bulat.foundation.model.tasks.SimpleTasksFactory
import ru.com.bulat.foundation.model.tasks.ThreadUtils
import ru.com.bulat.foundation.model.tasks.dispatchers.MainThreadDispatcher
import ru.com.bulat.mvvm_exemple.model.colors.InMemoryColorsRepository

class App : Application() , BaseApplication {

    private val tasksFactory = SimpleTasksFactory()
    private val threadUtils = ThreadUtils.Default()
    private val dispatcher = MainThreadDispatcher()

    /**
     * Place your repositories here, now we have only 1 repository
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        tasksFactory,
        dispatcher,
        InMemoryColorsRepository(tasksFactory, threadUtils)
    )
}