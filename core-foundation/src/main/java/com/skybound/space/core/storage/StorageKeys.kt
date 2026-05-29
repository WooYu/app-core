package com.skybound.space.core.storage

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object StorageKeys {
    fun stringKey(name: String): Preferences.Key<String> = stringPreferencesKey(name)
    fun intKey(name: String): Preferences.Key<Int> = intPreferencesKey(name)
    fun longKey(name: String): Preferences.Key<Long> = longPreferencesKey(name)
    fun boolKey(name: String): Preferences.Key<Boolean> = booleanPreferencesKey(name)
}
