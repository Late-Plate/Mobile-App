package com.example.late_plate.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.late_plate.viewModel.InventoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore("inventory_prefs")

class InventoryDataStore @Inject constructor(
    @ApplicationContext private val context: Context) {

    private val INVENTORY_KEY = stringPreferencesKey("inventory_items")

    suspend fun saveInventoryItems(items: List<InventoryItem>) {
        val jsonString = Gson().toJson(items)
        context.dataStore.edit { preferences ->
            preferences[INVENTORY_KEY] = jsonString
        }
    }

    val inventoryItemsFlow: Flow<List<InventoryItem>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[INVENTORY_KEY] ?: "[]"
        val type = object : TypeToken<List<InventoryItem>>() {}.type
        Gson().fromJson(jsonString, type) ?: emptyList()
    }
}
