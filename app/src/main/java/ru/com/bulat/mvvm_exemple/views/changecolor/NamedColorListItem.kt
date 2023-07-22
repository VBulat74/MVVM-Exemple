package ru.com.bulat.mvvm_exemple.views.changecolor

import ru.com.bulat.mvvm_exemple.model.colors.NamedColor

/**
 * Represents list item for the color; it may be selected or not
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)