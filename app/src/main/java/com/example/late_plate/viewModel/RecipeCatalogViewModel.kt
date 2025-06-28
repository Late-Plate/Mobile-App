package com.example.late_plate.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.network.RecipeCatalogClient
import com.example.late_plate.network.RecipeImageDescriptionClient
import com.example.late_plate.network.RecipeModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONException
import util.onError
import util.onSuccess
import javax.inject.Inject

@HiltViewModel
class RecipeCatalogViewModel @Inject constructor(
    private val recipeCatalogClient: RecipeCatalogClient,
    private val recipeImageDescriptionClient: RecipeImageDescriptionClient

): ViewModel() {
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _recipes = MutableStateFlow<Set<Recipe>>(emptySet())
    val recipes: StateFlow<Set<Recipe>> = _recipes.asStateFlow()
    private val _searchedRecipes = MutableStateFlow<Set<Recipe>>(emptySet())
    val searchedRecipes: StateFlow<Set<Recipe>> = _searchedRecipes.asStateFlow()
    fun getRecipes(page: Int, pageSize: Int) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = recipeCatalogClient.getRecipes(page, pageSize)
                response.onSuccess {
                    Log.d("content", it.content.toString())
                    val newRecipes = parseRecipes(it.content)
                    _recipes.value += newRecipes
                }.onError {
                    Log.e("RecipeCatalogViewModel", "API call failed: ${it}")
                    // Optionally show fallback UI or error message
                }
            } catch (e: Exception) {
                Log.e("RecipeCatalogViewModel", "Exception during recipe fetch ${e}")
                // Optionally handle the error (e.g., show Toast or update UI state)
            } finally {
                _isLoading.value = false
            }

            Log.d("RecipeCatalogViewModel", "getRecipes: ${recipes.value}")
        }
    }


    //    fun getRecipes(page: Int, pageSize: Int) {
//        if (_isLoading.value) return
//        viewModelScope.launch {
//            _isLoading.value = true
//            val response = recipeCatalogClient.getRecipes(page, pageSize)
//            response.onSuccess {
//                Log.d("content",it.content.toString())
//                val newRecipes = parseRecipes(it.content)
//                _recipes.value += newRecipes
//            }
//            _isLoading.value = false
//            Log.d("RecipeCatalogViewModel", "getRecipes: ${recipes.value}")
//        }
//    }
    fun searchRecipes(query: String, page: Int=0, pageSize: Int=10) {
        viewModelScope.launch {
            viewModelScope.launch {
                _isLoading.value = true
                val response = recipeCatalogClient.getRecipesByName(query, page, pageSize)
                response.onSuccess {
                    val newRecipes = parseRecipes(it.content)
                    if(page == 0){
                        _searchedRecipes.value = newRecipes
                    }
                    else{
                        _searchedRecipes.value += newRecipes
                    }
                }
                _isLoading.value = false
            }
        }
    }

    private suspend fun parseRecipes(recipeList: List<RecipeModel>): Set<Recipe> {
        return recipeList.map { model ->
            val ingredients = parseJsonListString(model.ingredients)
            val directions = parseJsonListString(model.instructions)
            val imageUrl = getImageUrl(model.name)
            val description = getRecipeDescription(model.name, directions.joinToString(" "))
            Recipe(
                title = model.name,
                ingredients = ingredients,
                directions = directions,
                imageUrl = imageUrl,
                time = " ",
                description = description
            )
        }.toSet()
    }

    fun parseJsonListString(jsonString: String): List<String> {
        return try {
            // First clean the string if needed
            val cleanJson = jsonString
                .replace("\\\"", "\"") // Unescape quotes if they're double-escaped
                .replace("\"{", "{")   // Remove unwanted quote prefixes
                .replace("}\"", "}")  // Remove unwanted quote suffixes

            Gson().fromJson(cleanJson, Array<String>::class.java).toList()
        } catch (e: Exception) {
            // Fallback to manual parsing if Gson fails
            manualParseJsonArray(jsonString)
        }
    }

    private fun manualParseJsonArray(jsonString: String): List<String> {
        Log.d("manualParseJsonArray", "manual")
        return try {
            // Remove the outer brackets and split by comma followed by quote
            val content = jsonString
                .removeSurrounding("[", "]")
                .split("\", \"")
                .map { it.removeSurrounding("\"") }

            content
        } catch (e: Exception) {
            emptyList() // Return empty list if parsing fails completely
        }
    }

    suspend fun getImageUrl(name: String): String {
        val imageUrl = try {
            recipeImageDescriptionClient.fetchImageUrlFromEdamam(name)
                ?: "https://via.placeholder.com/300"
        } catch (e: Exception) {
            Log.e("RecipeGenVM", "Image fetch failed", e)
            "https://via.placeholder.com/300"
        }
        return imageUrl;
    }
    suspend fun getRecipeDescription(title: String, directions: String): String {
        val description = try {
            recipeImageDescriptionClient.generateRecipeDescriptionGemini(title, directions)
                ?: "no description available"
        } catch (e: Exception) {
            Log.e("RecipeGenVM", "Description fetch failed", e)
            "no description available"
        }
        return description
    }
}