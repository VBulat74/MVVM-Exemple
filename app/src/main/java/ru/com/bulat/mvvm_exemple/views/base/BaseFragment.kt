package ru.com.bulat.mvvm_exemple.views.base

import androidx.fragment.app.Fragment
import ru.com.bulat.mvvm_exemple.MainActivity

/**
 * Base class for all fragments
 */
abstract class BaseFragment : Fragment() {

    /**
     * View-model that manages this fragment
     */
    abstract val viewModel: BaseViewModel

    /**
     * Call this method when activity controls (e.g. toolbar) should be re-rendered
     */
    fun notifyScreenUpdates() {
        // if you have more than 1 activity -> you should use a separate interface instead of direct
        // cast to MainActivity
        (requireActivity() as MainActivity).notifyScreenUpdates()
    }
}