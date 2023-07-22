package ru.com.bulat.mvvm_exemple

import android.app.Application
import ru.com.bulat.mvvm_exemple.model.colors.InMemoryColorsRepository

class App : Application() {

    /**
     * Place your repositories here, now we have only 1 repository
     */
    val models = listOf<Any>(
        InMemoryColorsRepository()
    )

}