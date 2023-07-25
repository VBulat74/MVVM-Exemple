package ru.com.bulat.mvvm_exemple.views.currentcolor

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.PendingResult
import ru.com.bulat.foundation.model.SuccessResult
import ru.com.bulat.foundation.model.takeSuccess
import ru.com.bulat.foundation.navigator.Navigator
import ru.com.bulat.foundation.uiactions.UiActions
import ru.com.bulat.foundation.views.BaseViewModel
import ru.com.bulat.foundation.views.LiveResult
import ru.com.bulat.foundation.views.MutableLiveResult
import ru.com.bulat.mvvm_exemple.R
import ru.com.bulat.mvvm_exemple.model.colors.ColorListener
import ru.com.bulat.mvvm_exemple.model.colors.ColorsRepository
import ru.com.bulat.mvvm_exemple.model.colors.NamedColor
import ru.com.bulat.mvvm_exemple.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }

    // --- example of listening results via model layer

    init {
        viewModelScope.launch {
            delay(2000)
            colorsRepository.addListener(colorListener)
            //_currentColor.postValue(ErrorResult(RuntimeException()))
        }
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)
    }

    // --- example of listening results directly from the screen

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
        }
    }

    // ---

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }


    fun tryAgain() {
        viewModelScope.launch {
            _currentColor.postValue(PendingResult())
            delay(2000)
            colorsRepository.addListener(colorListener)
        }
    }
}