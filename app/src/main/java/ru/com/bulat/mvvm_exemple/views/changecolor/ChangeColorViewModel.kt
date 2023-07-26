package ru.com.bulat.mvvm_exemple.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.FinalResult
import ru.com.bulat.foundation.model.PendingResult
import ru.com.bulat.foundation.model.SuccessResult
import ru.com.bulat.foundation.model.tasks.factories.TasksFactory
import ru.com.bulat.foundation.model.tasks.dispatchers.Dispatcher
import ru.com.bulat.foundation.navigator.Navigator
import ru.com.bulat.foundation.uiactions.UiActions
import ru.com.bulat.foundation.views.BaseViewModel
import ru.com.bulat.foundation.views.LiveResult
import ru.com.bulat.foundation.views.MediatorLiveResult
import ru.com.bulat.foundation.views.MutableLiveResult
import ru.com.bulat.mvvm_exemple.R
import ru.com.bulat.mvvm_exemple.model.colors.ColorsRepository
import ru.com.bulat.mvvm_exemple.model.colors.NamedColor
import ru.com.bulat.mvvm_exemple.views.changecolor.ChangeColorFragment.*

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    private val tasksFactory: TasksFactory,
    savedStateHandle: SavedStateHandle,
    dispatcher: Dispatcher,
) : BaseViewModel(dispatcher), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId = savedStateHandle.getLiveData("currentColorId", screen.currentColorId)
    private val _saveInProgress = MutableLiveData(false)

    // main destination (contains merged values from _availableColors & _currentColorId)
    private val _viewState = MediatorLiveResult<ViewState>(PendingResult())
    val viewState: LiveResult<ViewState> = _viewState

    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = viewState.map { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first {it.selected}
            uiActions.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.change_color_screen_title_simple)
        }
    }

    init {
        load()
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) {mergeSources()}
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {

        _saveInProgress.postValue(true)
        tasksFactory.async {
            val currentColorId = _currentColorId.value ?: throw IllegalStateException("Color ID should by NULL")
            val currentColor = colorsRepository.getById(currentColorId).await()
            colorsRepository.setCurrentColor(currentColor).await()
            return@async currentColor
        }
            .safeEnqueue(::onSaved)
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    /**
     * [MediatorLiveData] can listen other LiveData instances (even more than 1)
     * and combine their values.
     * Here we listen the list of available colors ([_availableColors] live-data) + current color id
     * ([_currentColorId] live-data), then we use both of these values in order to create a list of
     * [NamedColorListItem], it is a list to be displayed in RecyclerView.
     */
    private fun mergeSources() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveInProgress.value ?: return

        _viewState.value = colors.map { colorsList->
            ViewState(
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showSaveProgressBar = saveInProgress,
            )

        }
//        val currentColor = colors.first { it.id == currentColorId }
//        _screenTitle.value = uiActions.getString(R.string.change_color_screen_title, currentColor.name)
    }

    fun tryAgain() {
        load()
    }

    private fun onSaved(result: FinalResult<NamedColor>){
        _saveInProgress.value = false
        when(result){
            is SuccessResult ->{navigator.goBack(result.data)}
            is ErrorResult -> uiActions.toast(uiActions.getString(R.string.error_message))
        }
    }

    private fun load() {
        colorsRepository.getAvailableColors().into(_availableColors)
    }

    data class ViewState (
        val colorsList : List<NamedColorListItem>,
        val showSaveButton : Boolean,
        val showCancelButton : Boolean,
        val showSaveProgressBar : Boolean,

    )
}