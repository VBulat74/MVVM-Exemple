package ru.com.bulat.mvvm_exemple

import android.app.Application
import ru.com.bulat.foundation.BaseApplication

import ru.com.bulat.foundation.model.dispatchers.MainThreadDispatcher
import ru.com.bulat.mvvm_exemple.model.colors.InMemoryColorsRepository
import java.util.concurrent.Executors

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    /**
     * Place your singleton scope dependencies here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        InMemoryColorsRepository()
    )

}