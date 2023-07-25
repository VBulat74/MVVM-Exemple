package ru.com.bulat.mvvm_exemple.model.colors

import ru.com.bulat.foundation.model.Repository
import ru.com.bulat.foundation.model.tasks.Task

typealias ColorListener = (NamedColor) -> Unit

/**
 * Repository interface example.
 *
 * Provides access to the available colors and current selected color.
 */
interface ColorsRepository : Repository {

    /**
     * Get the list of all available colors that may be chosen by the user.
     */
    fun getAvailableColors(): Task<List<NamedColor>>

    /**
     * Get the color content by its ID
     */
    fun getById(id: Long): Task<NamedColor>

    /*
    * Get the current Color
     */
    fun getCurrentColor() : Task<NamedColor>

    /*
    * Set the specified Color as current
    * */
    fun setCurrentColor(color: NamedColor) : Task<Unit>

    /**
     * Listen for the current color changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addListener(listener: ColorListener)

    /**
     * Stop listening for the current color changes
     */
    fun removeListener(listener: ColorListener)

}