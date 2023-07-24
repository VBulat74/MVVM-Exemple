package ru.com.bulat.mvvm_exemple.views

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import ru.com.bulat.foundation.model.Result
import ru.com.bulat.foundation.views.BaseFragment
import ru.com.bulat.mvvm_exemple.R
import ru.com.bulat.mvvm_exemple.databinding.PartResultBinding


fun <T> BaseFragment.renderSimpleResult (root : ViewGroup, result: Result<T>, onSuccess : (T) -> Unit) {
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onPending = {binding.progressBar.visibility = View.VISIBLE},
        onError = {binding.errorContainer.visibility = View.VISIBLE},
        onSuccess = {successData ->
            root.children
                .filter { it.id != R.id.progressBar && it.id != R.id.errorContainer }
                .forEach { it.visibility = View.VISIBLE }
            onSuccess(successData)
        }
    )
}

fun BaseFragment.onTryAgain (root: View, onTryAgainPressed : () -> Unit) {
    root.findViewById<Button>(R.id.tryAgainButton)
        .setOnClickListener {
            onTryAgainPressed()
        }
}