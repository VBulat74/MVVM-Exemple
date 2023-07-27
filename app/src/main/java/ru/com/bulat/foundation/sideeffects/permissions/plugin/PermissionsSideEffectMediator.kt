package ru.com.bulat.foundation.sideeffects.permissions.plugin

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import ru.com.bulat.foundation.model.ErrorResult
import ru.com.bulat.foundation.model.tasks.Task
import ru.com.bulat.foundation.model.tasks.callback.CallbackTask
import ru.com.bulat.foundation.model.tasks.callback.Emitter
import ru.com.bulat.foundation.sideeffects.SideEffectMediator
import ru.com.bulat.foundation.sideeffects.permissions.Permissions

class PermissionsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<PermissionsSideEffectImpl>(), Permissions {

    val retainedState = RetainedState()

    override fun hasPermissions(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(permission: String): PermissionStatus = CallbackTask.create<PermissionStatus> { emitter ->
        if (retainedState.emitter != null) {
            emitter.emit(ErrorResult(IllegalStateException("Only one permission request can be active")))
            return@create
        }
        retainedState.emitter = emitter
        target { implementation ->
            implementation.requestPermission(permission)
        }
    }.suspend()

    class RetainedState(
        var emitter: Emitter<PermissionStatus>? = null
    )

}