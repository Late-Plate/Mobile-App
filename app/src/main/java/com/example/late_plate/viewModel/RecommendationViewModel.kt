package com.example.late_plate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.network.RecipeGenerationClient


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import util.Result

import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor
    (val recipeGenerationClient: RecipeGenerationClient)
    : ViewModel() {

    fun getRecipesBlocking(userId: Int): List<Recipe> {
        return runBlocking {
            val result = recipeGenerationClient.getTopRecipes(userId)
            when (result) {
                is Result.Success -> result.data
                else -> emptyList()
            }
        }
    }
}