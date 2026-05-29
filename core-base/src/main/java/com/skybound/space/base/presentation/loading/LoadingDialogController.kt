package com.skybound.space.base.presentation.loading

import androidx.fragment.app.FragmentManager

class LoadingDialogController(private val fragmentManager: FragmentManager) {

    private val tag = "FullscreenLoadingDialog"

    fun show() {
        if (fragmentManager.findFragmentByTag(tag) == null) {
            FullscreenLoadingDialogFragment()
                .show(fragmentManager, tag)
        }
    }

    fun hide() {
        (fragmentManager.findFragmentByTag(tag) as? FullscreenLoadingDialogFragment)
            ?.dismissAllowingStateLoss()
    }
}
