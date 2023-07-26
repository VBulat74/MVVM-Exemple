package ru.com.bulat.foundation.sideeffects.navigator.plugin

import ru.com.bulat.foundation.sideeffects.SideEffectMediator
import ru.com.bulat.foundation.sideeffects.navigator.Navigator
import ru.com.bulat.foundation.views.BaseScreen

class NavigatorSideEffectMediator : SideEffectMediator<Navigator>(), Navigator {

    override fun launch(screen: BaseScreen) = target {
        it.launch(screen)
    }

    override fun goBack(result: Any?) = target {
        it.goBack(result)
    }

}