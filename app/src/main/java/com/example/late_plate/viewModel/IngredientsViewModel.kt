package com.example.late_plate.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
class IngredientsViewModel(application: Application): AndroidViewModel(application)  {
    private val _ingredientsList = MutableStateFlow<List<String>>(emptyList())
    val ingredientsList=_ingredientsList.asStateFlow()

    init {
        loadJson()
    }

    private fun loadJson() {
        viewModelScope.launch (Dispatchers.IO){
            val jsonString = readJsonFile("ingredients.json")
            val words = parseJsonList(jsonString)
            _ingredientsList.value = words
        }
    }

    fun getMatchingIngredients(newValue: String): List<String>{
        return if (newValue.isNotEmpty()) {
            _ingredientsList.value.filter { it.contains(newValue, ignoreCase = true) }
                .sortedBy { it.equals(newValue, ignoreCase = true).not() } // Exact matches first
        } else {
            emptyList()
        }
    }

    private fun readJsonFile(fileName: String): String {
        return getApplication<Application>().assets.open(fileName).bufferedReader().use { it.readText() }

    }
    private fun parseJsonList(jsonString: String): List<String> {
     val gson = Gson()
     return gson.fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
    }
}