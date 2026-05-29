package com.skybound.space.base.ext

fun String.isEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isPhoneNumber(): Boolean =
    android.util.Patterns.PHONE.matcher(this).matches()
