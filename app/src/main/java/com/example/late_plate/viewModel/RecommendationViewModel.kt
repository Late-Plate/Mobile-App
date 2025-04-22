package com.example.late_plate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.network.RecipeGenerationClient


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import util.Result

import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor
    (val recipeGenerationClient: RecipeGenerationClient) : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes=_recipes.asStateFlow()

    fun getRecipesBlocking(userId: Int){
        runBlocking {
            val result = recipeGenerationClient.getTopRecipes(userId)
            when (result) {
                is Result.Success -> _recipes.value=result.data
                else -> _recipes.value= dummyRecipes
            }
        }
    }
}