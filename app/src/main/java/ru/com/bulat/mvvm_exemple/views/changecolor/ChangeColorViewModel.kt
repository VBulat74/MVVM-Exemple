package ru.com.bulat.mvvm_exemple.views.changecolor

import androidx.lifecycle.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.com.bulat.foundation.model.*
import ru.com.bulat.foundation.model.coroutines.EmptyProgress
import ru.com.bulat.foundation.model.coroutines.PercentageProgress
import ru.com.bulat.foundation.model.coroutines.Progress
import ru.com.bulat.foundation.model.coroutines.getPercentage
import ru.com.bulat.foundation.model.coroutines.isInProgress
import ru.com.bulat.foundation.sideeffects.navigator.Navigator
import ru.com.bulat.foundation.sideeffects.resources.Resources
import ru.com.bulat.foundation.sideeffects.toasts.Toasts
import ru.com.bulat.foundation.utils.finiteShareIn
import ru.com.bulat.foundation.views.BaseViewModel
import ru.com.bulat.foundation.views.ResultFlow
import ru.com.bulat.foundation.views.ResultMutableStateFlow
import ru.com.bulat.mvvm_exemple.R
import ru.com.bulat.mvvm_exemple.model.colors.ColorsRepository
import ru.com.bulat.mvvm_exemple.model.colors.NamedColor
import ru.com.bulat.mvvm_exemple.views.changecolor.ChangeColorFragment.Screen

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors: ResultMutableStateFlow<List<NamedColor>> = MutableStateFlow(PendingResult())
    private val _currentColorId = savedStateHandle.getStateFlow1("currentColorId", screen.currentColorId)
    private val _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)

    // main destination (contains merged values from _availableColors & _currentColorId & _saveInProgress)
    val viewState: ResultFlow<ViewState> = combine(
        _availableColors,
        _currentColorId,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )

    // example of converting Flow into LiveData
    // - incoming flow is Flow<Result<ViewState>>
    // - Flow<Result<ViewState>> is mapped to Flow<String> by using .map() operator
    // - then Flow<String> is converted to LiveData<String> by using .asLiveData()
    val screenTitle: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) {
                val currentColor = result.data.colorsList.first { it.selected }
                resources.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
            } else {
                resources.getString(R.string.change_color_screen_title_simple)
            }
        }
        .asLiveData()

    init {
        load()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_instantSaveInProgress.value.isInProgress()) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _instantSaveInProgress.value = PercentageProgress.START
            _sampledSaveInProgress.value = PercentageProgress.START

            val currentColorId = _currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)

            val flow = colorsRepository.setCurrentColor(currentColor).finiteShareIn(this)

            val instantJob = async {
                flow.collect { percentage -> _instantSaveInProgress.value = PercentageProgress(percentage) }
            }

            val sampledJob = async {
                flow.sample(200) // emit the most actual progress every 200ms.
                    .collect { percentage -> _sampledSaveInProgress.value = PercentageProgress(percentage) }
            }

            instantJob.await()
            sampledJob.await()

            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException) toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }

    /**
     * Transformation pure method for combining data from several input flows:
     * - the result of fetching colors list (Result<List<NamedColor>>)
     * - current selected color in RecyclerView (Long)
     * - [Progress] instance which indicates whether saving operation is in
     *   progress or not
     * All values above are merged into one [ViewState] instance:
     * ```
     * Flow<Result<List<NamedColor>>> ---+
     * Flow<Long> -----------------------|--> Flow<Result<ViewState>>
     * Flow<Progress> -------------------+
     * ```
     */
    private fun mergeSources(colors: Result<List<NamedColor>>, currentColorId: Long,
                             instantSaveInProgress: Progress, sampledSaveInProgress: Progress): Result<ViewState> {
        // map Result<List<NamedColor>> to Result<ViewState>
        return colors.map { colorsList ->
            ViewState(
                // map List<NamedColor> to List<NamedColorListItem>
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },

                showSaveButton = !instantSaveInProgress.isInProgress(),
                showCancelButton = !instantSaveInProgress.isInProgress(),
                showSaveProgressBar = instantSaveInProgress.isInProgress(),

                saveProgressPercentage = instantSaveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(R.string.percentage_value, sampledSaveInProgress.getPercentage())
            )
        }
    }

    private fun load() = into(_availableColors) { colorsRepository.getAvailableColors() }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean,

        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String
    )

}