package ru.com.bulat.foundation.sideeffects.dialogs.plugin

import kotlinx.coroutines.suspendCancellableCoroutine
import ru.com.bulat.foundation.model.Emitter
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.toEmitter
import ru.com.bulat.foundation.sideeffects.SideEffectMediator
import ru.com.bulat.foundation.sideeffects.dialogs.Dialogs

class DialogsSideEffectMediator : SideEffectMediator<DialogsSideEffectImpl>(), Dialogs {

    var retainedState = RetainedState()

    override suspend fun show(dialogConfig: DialogConfig): Boolean = suspendCancellableCoroutine { continuation ->
        val emitter = continuation.toEmitter()
        if (retainedState.record != null) {
            // for now allowing only 1 active dialog at a time
            emitter.emit(ErrorResult(IllegalStateException("Can't launch more than 1 dialog at a time")))
            return@suspendCancellableCoroutine
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
    }

    class DialogRecord(
        val emitter: Emitter<Boolean>,
        val config: DialogConfig
    )

    class RetainedState(
        var record: DialogRecord? = null
    )
}