package com.riveronly.wanandroid.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 定义 Context 扩展属性来初始化 DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object DataStoreHelper {

    private lateinit var dataStore: DataStore<Preferences>

    fun init(context: Context) {
        dataStore = context.dataStore
    }

    suspend fun putString(key: String, value: String) {
        dataStore.edit { settings ->
            settings[stringPreferencesKey(key)] = value
        }
    }

    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { settings ->
            settings[intPreferencesKey(key)] = value
        }
    }

    suspend fun putLong(key: String, value: Long) {
        dataStore.edit { settings ->
            settings[longPreferencesKey(key)] = value
        }
    }

    suspend fun putDouble(key: String, value: Double) {
        dataStore.edit { settings ->
            settings[doublePreferencesKey(key)] = value
        }
    }

    suspend fun putFloat(key: String, value: Float) {
        dataStore.edit { settings ->
            settings[floatPreferencesKey(key)] = value
        }
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { settings ->
            settings[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun putStringSet(key: String, value: Set<String>) {
        val serializedValue = value.joinToString(",")
        dataStore.edit { settings ->
            settings[stringPreferencesKey(key)] = serializedValue
        }
    }

    fun getString(key: String, defaultValue: String): Flow<String> {
        return dataStore.data.map { settings ->
            settings[stringPreferencesKey(key)] ?: defaultValue
        }
    }

    fun getInt(key: String, defaultValue: Int): Flow<Int> {
        return dataStore.data.map { settings ->
            settings[intPreferencesKey(key)] ?: defaultValue
        }
    }

    fun getLong(key: String, defaultValue: Long): Flow<Long> {
        return dataStore.data.map { settings ->
            settings[longPreferencesKey(key)] ?: defaultValue
        }
    }

    fun getDouble(key: String, defaultValue: Double): Flow<Double> {
        return dataStore.data.map { settings ->
            settings[doublePreferencesKey(key)] ?: defaultValue
        }
    }

    fun getFloat(key: String, defaultValue: Float): Flow<Float> {
        return dataStore.data.map { settings ->
            settings[floatPreferencesKey(key)] ?: defaultValue
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> {
        return dataStore.data.map { settings ->
            settings[booleanPreferencesKey(key)] ?: defaultValue
        }
    }

    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>> {
        return dataStore.data.map { settings ->
            val serializedValue = settings[stringPreferencesKey(key)] ?: ""
            if (serializedValue.isEmpty()) defaultValue else serializedValue.split(",").toSet()
        }
    }

    suspend fun removeKey(key: String) {
        dataStore.edit { settings ->
            settings.remove(stringPreferencesKey(key))
        }
    }

    suspend fun clearAll() {
        dataStore.edit { settings ->
            settings.clear()
        }
    }
}