package ru.com.bulat.foundation.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.PendingResult
import ru.com.bulat.foundation.model.SuccessResult
import ru.com.bulat.foundation.model.Result

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
        (requireActivity() as FragmentsHolder).notifyScreenUpdates()
    }
    fun <T> renderResult(root : ViewGroup, result: Result<T>,
                         onPending : () -> Unit,
                         onError : (Exception) -> Unit,
                         onSuccess : (T) -> Unit
    ) {
        root.children.forEach { it.visibility = View.GONE }
        when(result) {
            is SuccessResult -> onSuccess(result.data)
            is ErrorResult -> onError(result.exception)
            is PendingResult -> onPending()
        }
    }
}