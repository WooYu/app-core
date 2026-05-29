package com.skybound.space.core.navigation

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.skybound.space.base.presentation.navigation.NavDestination

class NavigationManager(private val activity: FragmentActivity) {

    fun navigateTo(destination: NavDestination) {
        // App layer overrides or uses NavController
    }

    fun navigateWithIntent(intent: Intent) {
        activity.startActivity(intent)
    }

    fun navigateBack() {
        activity.onBackPressedDispatcher.onBackPressed()
    }
}
