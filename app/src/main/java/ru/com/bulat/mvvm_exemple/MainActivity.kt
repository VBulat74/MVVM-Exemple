package ru.com.bulat.mvvm_exemple

import android.os.Bundle
import ru.com.bulat.foundation.sideeffects.navigator.plugin.StackFragmentNavigator
import ru.com.bulat.foundation.sideeffects.navigator.plugin.NavigatorPlugin
import ru.com.bulat.foundation.sideeffects.SideEffectPluginsManager
import ru.com.bulat.foundation.sideeffects.dialogs.plugin.DialogsPlugin
import ru.com.bulat.foundation.sideeffects.intents.plugin.IntentsPlugin
import ru.com.bulat.foundation.sideeffects.permissions.plugin.PermissionsPlugin
import ru.com.bulat.foundation.sideeffects.resources.plugin.ResourcesPlugin
import ru.com.bulat.foundation.sideeffects.toasts.plugin.ToastsPlugin
import ru.com.bulat.foundation.views.activity.BaseActivity
import ru.com.bulat.mvvm_exemple.views.currentcolor.CurrentColorFragment


/**
 * This application is a single-activity app. MainActivity is a container
 * for all screens.
 */
class MainActivity : BaseActivity() {

    override fun registerPlugins(manager: SideEffectPluginsManager) = with (manager) {
        val navigator = createNavigator()
        register(ToastsPlugin())
        register(ResourcesPlugin())
        register(NavigatorPlugin(navigator))
        register(PermissionsPlugin())
        register(DialogsPlugin())
        register(IntentsPlugin())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Initializer.initDependencies()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun createNavigator() = StackFragmentNavigator(
        containerId = R.id.fragmentContainer,
        defaultTitle = getString(R.string.app_name),
        animations = StackFragmentNavigator.Animations(
            enterAnim = R.anim.enter,
            exitAnim = R.anim.exit,
            popEnterAnim = R.anim.pop_enter,
            popExitAnim = R.anim.pop_exit
        ),
        initialScreenCreator = { CurrentColorFragment.Screen() }
    )

}