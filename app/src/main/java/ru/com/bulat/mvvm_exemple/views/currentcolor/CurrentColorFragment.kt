package ru.com.bulat.mvvm_exemple.views.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.com.bulat.foundation.views.BaseFragment
import ru.com.bulat.foundation.views.BaseScreen
import ru.com.bulat.foundation.views.screenViewModel
import ru.com.bulat.mvvm_exemple.databinding.FragmentCurrentColorBinding
import ru.com.bulat.mvvm_exemple.databinding.PartResultBinding
import ru.com.bulat.mvvm_exemple.views.onTryAgain
import ru.com.bulat.mvvm_exemple.views.renderSimpleResult

class CurrentColorFragment : BaseFragment() {

    // no arguments for this screen
    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentCurrentColorBinding.inflate(inflater, container, false)

        viewModel.currentColor.observe(viewLifecycleOwner) {result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {binding.colorView.setBackgroundColor(it.value)}
            )

            /*renderResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    binding.colorContainer.visibility =View.VISIBLE
                    binding.changeColorButton.visibility = View.VISIBLE

                    binding.colorView.setBackgroundColor(it.value)
                },
                onError = {
                    resultBinding.errorContainer.visibility = View.VISIBLE
                },
                onPending = {resultBinding.progressBar.visibility = View.VISIBLE}
            )*/

/*
            when (result) {
                is PendingResult -> {
                    resultBinding.progressBar.visibility = View.VISIBLE
                    resultBinding.errorContainer.visibility = View.GONE
                    binding.colorContainer.visibility =View.GONE
                    binding.changeColorButton.visibility = View.GONE
                }
                is ErrorResult -> {
                    resultBinding.progressBar.visibility = View.GONE
                    resultBinding.errorContainer.visibility = View.VISIBLE
                    binding.colorContainer.visibility =View.GONE
                    binding.changeColorButton.visibility = View.GONE
                }
                is SuccessResult -> {
                    resultBinding.progressBar.visibility = View.GONE
                    resultBinding.errorContainer.visibility = View.GONE
                    binding.colorContainer.visibility =View.VISIBLE
                    binding.changeColorButton.visibility = View.VISIBLE

                    binding.colorView.setBackgroundColor(result.data.value)
                }
            }
*/

        }

        binding.changeColorButton.setOnClickListener {
            viewModel.changeColor()
        }

        onTryAgain (binding.root) {
            viewModel.tryAgain()
        }

        return binding.root
    }


}