package ru.com.bulat.foundation

import androidx.lifecycle.ViewModel
import ru.com.bulat.foundation.navigator.IntermediateNavigator
import ru.com.bulat.foundation.navigator.Navigator
import ru.com.bulat.foundation.uiactions.UiActions

/**
 * Implementation of [Navigator] and [UiActions].
 * It is based on activity view-model because instances of [Navigator] and [UiActions]
 * should be available from fragments' view-models (usually they are passed to the view-model constructor).
 */
class ActivityScopeViewModel(
    val uiActions: UiActions,
    val navigator: IntermediateNavigator
) : ViewModel(),
    Navigator by navigator,
    UiActions by uiActions {

    override fun onCleared() {
        super.onCleared()
        navigator.clear()
    }

}