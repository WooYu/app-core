package com.skybound.space.core.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPrefsManager(context: Context, name: String = "secure_prefs") {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        name,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getString(key: String, default: String? = null): String? =
        prefs.getString(key, default)

    fun putString(key: String, value: String) =
        prefs.edit().putString(key, value).apply()

    fun remove(key: String) =
        prefs.edit().remove(key).apply()

    fun clearAll() =
        prefs.edit().clear().apply()
}
