package ru.com.bulat.foundation

import androidx.lifecycle.ViewModel
import ru.com.bulat.foundation.navigator.IntermediateNavigator
import ru.com.bulat.foundation.navigator.Navigator
import ru.com.bulat.foundation.sideeffects.SideEffectMediator
import ru.com.bulat.foundation.sideeffects.SideEffectMediatorsHolder
import ru.com.bulat.foundation.uiactions.UiActions

/**
 * Holder for side-effect mediators.
 * It is based on activity view-model because instances of side-effect mediators
 * should be available from fragments' view-models (usually they are passed to the view-model constructor).
 */
class ActivityScopeViewModel : ViewModel() {

    internal val sideEffectMediatorsHolder = SideEffectMediatorsHolder()

    // contains the list of side-effect mediators that can be
    // passed to view-model constructors
    val sideEffectMediators: List<SideEffectMediator<*>>
        get() = sideEffectMediatorsHolder.mediators

    override fun onCleared() {
        super.onCleared()
        sideEffectMediatorsHolder.clear()
    }

}