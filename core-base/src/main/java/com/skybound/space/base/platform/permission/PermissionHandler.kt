package com.skybound.space.base.platform.permission

interface PermissionHandler {
    fun requestPermission(permission: String, onGranted: () -> Unit, onDenied: () -> Unit)
    fun requestPermissions(
        permissions: Array<String>,
        onAllGranted: () -> Unit,
        onDenied: (List<String>) -> Unit
    )
    fun hasPermission(permission: String): Boolean
}
