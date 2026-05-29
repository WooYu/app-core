package com.skybound.space.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class DataStoreManager(private val context: Context) {

    fun <T> get(key: Preferences.Key<T>, default: T): Flow<T> =
        context.dataStore.data.map { prefs -> prefs[key] ?: default }

    suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { prefs -> prefs[key] = value }
    }

    suspend fun <T> remove(key: Preferences.Key<T>) {
        context.dataStore.edit { prefs -> prefs.remove(key) }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
