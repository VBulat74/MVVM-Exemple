package ru.com.bulat.mvvm_exemple.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.PendingResult
import ru.com.bulat.foundation.model.SuccessResult
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
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId = savedStateHandle.getLiveData("currentColorId", screen.currentColorId)

    // main destination (contains merged values from _availableColors & _currentColorId)
    private val _colorsList = MediatorLiveResult<List<NamedColorListItem>>(PendingResult())
    val colorsList: LiveResult<List<NamedColorListItem>> = _colorsList

    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = colorsList.map { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.first {it.selected}
            uiActions.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.change_color_screen_title_simple)
        }
    }

    init {
        viewModelScope.launch {
            delay(2000)
            _availableColors.value = ErrorResult(RuntimeException())
            //_availableColors.value = SuccessResult(colorsRepository.getAvailableColors())
            // initializing MediatorLiveData
        }
        _colorsList.addSource(_availableColors) { mergeSources() }
        _colorsList.addSource(_currentColorId) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        //
        val currentColorId = _currentColorId.value ?: return
        val currentColor = colorsRepository.getById(currentColorId)
        colorsRepository.currentColor = currentColor
        navigator.goBack(result = currentColor)
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

        _colorsList.value = colors.map {colorsList->
            colorsList.map { NamedColorListItem(it, currentColorId == it.id) }
        }
//        val currentColor = colors.first { it.id == currentColorId }
//        _screenTitle.value = uiActions.getString(R.string.change_color_screen_title, currentColor.name)
    }

    fun tryAgain() {
        viewModelScope.launch {
            _availableColors.postValue(PendingResult())
            delay(2000)
            _availableColors.postValue(SuccessResult(colorsRepository.getAvailableColors()))
        }
    }

}