package ru.com.bulat.foundation.sideeffects.dialogs.plugin

import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.tasks.Task
import ru.com.bulat.foundation.model.tasks.callback.CallbackTask
import ru.com.bulat.foundation.model.tasks.callback.Emitter
import ru.com.bulat.foundation.sideeffects.SideEffectMediator
import ru.com.bulat.foundation.sideeffects.dialogs.Dialogs

class DialogsSideEffectMediator : SideEffectMediator<DialogsSideEffectImpl>(), Dialogs {

    var retainedState = RetainedState()

    override suspend fun show(dialogConfig: DialogConfig): Boolean = CallbackTask.create<Boolean> { emitter ->
        if (retainedState.record != null) {
            // for now allowing only 1 active dialog at a time
            emitter.emit(ErrorResult(IllegalStateException("Can't launch more than 1 dialog at a time")))
            return@create
        }

        val wrappedEmitter = Emitter.wrap(emitter) {
            retainedState.record = null
        }

        val record = DialogRecord(wrappedEmitter, dialogConfig)
        wrappedEmitter.setCancelListener {
            target { implementation ->
                implementation.removeDialog()
            }
        }

        target { implementation ->
            implementation.showDialog(record)
        }

        retainedState.record = record
    }.suspend()

    class DialogRecord(
        val emitter: Emitter<Boolean>,
        val config: DialogConfig
    )

    class RetainedState(
        var record: DialogRecord? = null
    )
}