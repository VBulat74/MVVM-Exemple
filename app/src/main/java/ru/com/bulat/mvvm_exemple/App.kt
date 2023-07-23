package ru.com.bulat.mvvm_exemple

import android.app.Application
import ru.com.bulat.foundation.BaseApplication
import ru.com.bulat.foundation.model.Repository
import ru.com.bulat.mvvm_exemple.model.colors.InMemoryColorsRepository

class App : Application() , BaseApplication {

    /**
     * Place your repositories here, now we have only 1 repository
     */
    override val repositories: List<Repository> = listOf<Repository>(
        InMemoryColorsRepository()
    )
}