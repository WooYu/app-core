package com.skybound.space.base.platform.permission

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class ActivityPermissionHandler(private val activity: ComponentActivity) : PermissionHandler {

    private var onGranted: (() -> Unit)? = null
    private var onDenied: (() -> Unit)? = null
    private var onAllGranted: (() -> Unit)? = null
    private var onMultiDenied: ((List<String>) -> Unit)? = null

    private val singleLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted?.invoke() else onDenied?.invoke()
    }

    private val multiLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val denied = results.filterValues { !it }.keys.toList()
        if (denied.isEmpty()) onAllGranted?.invoke() else onMultiDenied?.invoke(denied)
    }

    override fun requestPermission(
        permission: String,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        this.onGranted = onGranted
        this.onDenied = onDenied
        if (hasPermission(permission)) onGranted() else singleLauncher.launch(permission)
    }

    override fun requestPermissions(
        permissions: Array<String>,
        onAllGranted: () -> Unit,
        onDenied: (List<String>) -> Unit
    ) {
        this.onAllGranted = onAllGranted
        this.onMultiDenied = onDenied
        val missing = permissions.filter { !hasPermission(it) }.toTypedArray()
        if (missing.isEmpty()) onAllGranted() else multiLauncher.launch(missing)
    }

    override fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
}
