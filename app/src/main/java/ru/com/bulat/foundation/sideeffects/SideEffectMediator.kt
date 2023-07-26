package ru.com.bulat.foundation.sideeffects

import ru.com.bulat.foundation.model.tasks.dispatchers.Dispatcher
import ru.com.bulat.foundation.model.tasks.dispatchers.MainThreadDispatcher
import ru.com.bulat.foundation.utils.ResourceActions

/**
 * Base class for all side-effect mediators.
 * These mediators live in [ActivityScopeViewModel].
 * Mediator should delegate all UI-related logic to the implementations via [target] field.
 */
open class SideEffectMediator<Implementation>(
    dispatcher: Dispatcher = MainThreadDispatcher()
) {

    protected val target = ResourceActions<Implementation>(dispatcher)

    /**
     * Assign/unassign the target implementation for this provder.
     */
    fun setTarget(target: Implementation?) {
        this.target.resource = target
    }

    fun clear() {
        target.clear()
    }

}