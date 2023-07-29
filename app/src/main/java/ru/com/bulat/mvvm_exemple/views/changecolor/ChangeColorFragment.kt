package ru.com.bulat.mvvm_exemple.views.changecolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.com.bulat.foundation.views.BaseFragment
import ru.com.bulat.foundation.views.BaseScreen
import ru.com.bulat.foundation.views.HasScreenTitle
import ru.com.bulat.foundation.views.screenViewModel
import ru.com.bulat.mvvm_exemple.R
import ru.com.bulat.mvvm_exemple.databinding.FragmentChangeColorBinding
import ru.com.bulat.mvvm_exemple.views.onTryAgain
import ru.com.bulat.mvvm_exemple.views.renderSimpleResult

/**
 * Screen for changing color.
 * 1) Displays the list of available colors
 * 2) Allows choosing the desired color
 * 3) Chosen color is saved only after pressing "Save" button
 * 4) The current choice is saved via [SavedStateHandle] (see [ChangeColorViewModel])
 */
class ChangeColorFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: color ID to be displayed as selected.
     */
    class Screen(
        val currentColorId: Long
    ) : BaseScreen

    override val viewModel by screenViewModel<ChangeColorViewModel>()

    /**
     * Example of dynamic screen title
     */
    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentChangeColorBinding.inflate(inflater, container, false)

        val adapter = ColorsAdapter(viewModel)
        setupLayoutManager(binding, adapter)

        binding.saveButton.setOnClickListener { viewModel.onSavePressed() }
        binding.cancelButton.setOnClickListener { viewModel.onCancelPressed() }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.viewState.collect {result ->
                    renderSimpleResult(binding.root, result) {viewState ->
/*
                        adapter.items = viewState.colorsList
                        binding.saveButton.visibility = if (viewState.showSaveButton) View.VISIBLE else View.INVISIBLE
                        binding.cancelButton.visibility = if (viewState.showCancelButton) View.VISIBLE else View.INVISIBLE

                        binding.saveProgressGroup.visibility = if (viewState.showSaveProgressBar) View.VISIBLE else View.GONE
                        binding.saveProgressBar.progress = viewState.saveProgressPercentage
                        binding.savingPercentageTextView.text = viewState.saveProgressPercentageMessage
*/

                        adapter.items = viewState.colorsList
                        binding.saveButton.visibility = if (viewState.showSaveButton) View.VISIBLE else View.INVISIBLE
                        binding.cancelButton.visibility = if (viewState.showCancelButton) View.VISIBLE else View.INVISIBLE
                        binding.saveProgressBar.visibility = if (viewState.showSaveProgressBar) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        viewModel.screenTitle.observe(viewLifecycleOwner) {
            // if screen title is changed -> need to notify activity about updates
            notifyScreenUpdates()
        }

        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }

        return binding.root
    }

    private fun setupLayoutManager(binding: FragmentChangeColorBinding, adapter: ColorsAdapter) {
        // waiting for list width
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.root.width
                val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)
                val columns = width / itemWidth
                binding.colorsRecyclerView.adapter = adapter
                binding.colorsRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
            }
        })
    }
}