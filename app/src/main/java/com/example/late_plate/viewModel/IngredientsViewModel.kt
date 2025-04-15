package com.example.late_plate.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(val application: Application): ViewModel()  {
    private val _ingredientsList = MutableStateFlow<List<String>>(emptyList())
    val ingredientsList=_ingredientsList.asStateFlow()

    init {
        loadJson()
    }

    private fun loadJson() {
        viewModelScope.launch (Dispatchers.IO){
            val jsonString = readJsonFile("unique_ner.json")
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
        return application.assets.open(fileName).bufferedReader().use { it.readText() }

    }
    private fun parseJsonList(jsonString: String): List<String> {
     val gson = Gson()
     return gson.fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
    }
}