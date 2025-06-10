package com.example.late_plate.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.network.RecipeGenerationClient
import com.example.late_plate.network.RecipeImageDescriptionClient


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import util.Result

import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recipeGenerationClient: RecipeGenerationClient,
    private val recipeImageDescriptionClient: RecipeImageDescriptionClient
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    fun getRecipesBlocking(userId: Int) {
        viewModelScope.launch {
            try {
                val result = recipeGenerationClient.getTopRecipes(userId)

                val rawRecipes = when (result) {
                    is Result.Success -> result.data
                    else -> {
                        Log.e("RecommendationVM", "Failed to get recipes, using dummy")
                        dummyRecipes
                    }
                }

                // Immediately show recipes without descriptions/images
                _recipes.value = rawRecipes.map {
                    it.copy(description = "Loading description...", imageUrl = "loading_image_url")
                }

                // Then enrich each recipe asynchronously
                rawRecipes.forEachIndexed { index, recipe ->
                    launch {
                        try {
                            // Debug log before description call
                            Log.d("RecommendationVM", "Starting description fetch for ${recipe.title}")

                            // Update description first
                            val description = recipeImageDescriptionClient.generateRecipeDescriptionGemini(
                                recipe.title,
                                recipe.directions.joinToString(" ")
                            ) ?: "No description available"

                            Log.d("RecommendationVM", "Description received for ${recipe.title}: $description")
                            updateRecipe(index) { it.copy(description = description) }

                            // Debug log before image call
                            Log.d("RecommendationVM", "Starting image fetch for ${recipe.title}")

                            // Then update image
                            val imageUrl = recipeImageDescriptionClient.fetchImageUrlFromEdamam(recipe.title)
                                ?: "https://via.placeholder.com/300"

                            Log.d("RecommendationVM", "Image URL received for ${recipe.title}: $imageUrl")
                            updateRecipe(index) { it.copy(imageUrl = imageUrl) }

                        } catch (e: Exception) {
                            Log.e("RecommendationVM", "Error enriching recipe ${recipe.title}", e)
                            updateRecipe(index) {
                                it.copy(
                                    description = "Description unavailable: ${e.message}",
                                    imageUrl = "https://via.placeholder.com/300"
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RecommendationVM", "Error getting recipes", e)
                _recipes.value = dummyRecipes
            }
        }
    }

    private fun updateRecipe(index: Int, transform: (Recipe) -> Recipe) {
        val currentList = _recipes.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = transform(currentList[index])
            _recipes.value = currentList
        }
    }

//    fun getRecipesBlocking(userId: Int) {
//        viewModelScope.launch {
//            try {
//                // First fetch the basic recipes
//                val result = recipeGenerationClient.getTopRecipes(userId)
//
//                val rawRecipes = when (result) {
//                    is Result.Success -> result.data
//                    else -> {
//                        Log.e("RecommendationVM", "Failed to get recipes, using dummy")
//                        dummyRecipes
//                    }
//                }
//
//                // Immediately show recipes without descriptions/images
//                _recipes.value = rawRecipes.map { it.copy(description = "Loading...", imageUrl = "") }
//
//                // Then enrich each recipe asynchronously
//                rawRecipes.forEach { recipe ->
//                    launch {
//                        try {
//                            // Update description first
//                            val description = recipeImageDescriptionClient.generateRecipeDescriptionGemini(
//                                recipe.title,
//                                recipe.directions.joinToString(" ")
//                            ) ?: "No description available"
//
//                            updateRecipe(recipe.id) { it.copy(description = description) }
//
//                            // Then update image
//                            val imageUrl = recipeImageDescriptionClient.fetchImageUrlFromUnsplash(recipe)
//                                ?: "https://via.placeholder.com/300"
//
//                            updateRecipe(recipe.id) { it.copy(imageUrl = imageUrl) }
//                        } catch (e: Exception) {
//                            Log.e("RecommendationVM", "Error enriching recipe ${recipe.title}", e)
//                            updateRecipe(recipe.id) {
//                                it.copy(
//                                    description = "Description unavailable",
//                                    imageUrl = "https://via.placeholder.com/300"
//                                )
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("RecommendationVM", "Error getting recipes", e)
//                _recipes.value = dummyRecipes
//            }
//        }
//    }
//
//    private fun updateRecipe(recipeId: Int, transform: (Recipe) -> Recipe) {
//        _recipes.value = _recipes.value.map { recipe ->
//            if (recipe.id == recipeId) transform(recipe) else recipe
//        }
//    }

//    fun getRecipesBlocking(userId: Int) {
//        viewModelScope.launch {
//            try {
//                val result = recipeGenerationClient.getTopRecipes(userId)
//
//                val rawRecipes = when (result) {
//                    is Result.Success -> result.data
//                    else -> {
//                        Log.e("RecommendationVM", "Failed to get recipes, using dummy")
//                        dummyRecipes
//                    }
//                }
//
//                val enrichedRecipes = rawRecipes.map { recipe ->
//                    try {
//                        val description = recipeImageDescriptionClient.generateRecipeDescriptionGemini(
//                            recipe.title,
//                            recipe.directions.joinToString(" ")
//                        ) ?: "No description available"
//
//                        val imageUrl = recipeImageDescriptionClient.fetchImageUrlFromUnsplash(recipe)
//                            ?: "https://via.placeholder.com/300"
//
//                        recipe.copy(
//                            description = description,
//                            imageUrl = imageUrl
//                        )
//                    } catch (e: Exception) {
//                        Log.e("RecommendationVM", "Error enriching recipe ${recipe.title}", e)
//                        recipe.copy(
//                            description = "Description unavailable",
//                            imageUrl = "https://via.placeholder.com/300"
//                        )
//                    }
//                }
//
//                _recipes.value = enrichedRecipes
//            } catch (e: Exception) {
//                Log.e("RecommendationVM", "Error getting recipes", e)
//                _recipes.value = dummyRecipes
//            }
//        }
//    }

}
