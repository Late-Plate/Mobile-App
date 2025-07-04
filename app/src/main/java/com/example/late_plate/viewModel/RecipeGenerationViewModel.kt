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
import kotlinx.coroutines.runBlocking
import util.onError
import util.onSuccess
import javax.inject.Inject

@HiltViewModel
class RecipeGenerationViewModel @Inject constructor(
    val recipeGenerationClient: RecipeGenerationClient,
    val recipeImageDescriptionClient: RecipeImageDescriptionClient
) : ViewModel() {
    private val _recipeStart="<RECIPE_START>"
    private val _inputStart="<INPUT_START>"
    private val _nextInput="<NEXT_INPUT>"
    private val _inputEnd="<INPUT_END>"
    private val _ingredientStart="<INGR_START>"
    private val _nextIngredient="<NEXT_INGR>"
    private val _ingredientEnd="<INGR_END>"
    private val _instructionStart="<INSTR_START>"
    private val _nextInstruction="<NEXT_INSTR>"
    private val _instructionEnd="<INSTR_END>"
    private val _titleStart="<TITLE_START>"
    private val _titleEnd="<TITLE_END>"
    private val _isSendingRequest = MutableStateFlow(false)
    val isSendingRequest = _isSendingRequest.asStateFlow()
    private val _bothFailed = MutableStateFlow(false)
    val bothFailed = _bothFailed.asStateFlow()
    private val _recipeState = MutableStateFlow<MutableList<Recipe>>(mutableListOf())
    val recipeState: StateFlow<MutableList<Recipe>> = _recipeState.asStateFlow()
    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
    val ingredients=_ingredients.asStateFlow()
    private val _selectedIngredients = MutableStateFlow<List<String>>(emptyList())
    val selectedIngredients=_selectedIngredients.asStateFlow()
    fun setIngredients(ingredients:List<String>){
        _ingredients.value=ingredients
    }
    fun setSelectedIngredients(ingredients:List<String>){
        _selectedIngredients.value=ingredients
    }
    fun resetRecipe(){
        _recipeState.value= mutableListOf()
    }
    fun getResponse(){
        viewModelScope.launch {
            _recipeState.value= parseList()
        }
    }
    fun resetBothFailed(){
        _bothFailed.value = false
    }
    suspend fun parseList(): MutableList<Recipe> {
        var response1Fail = false
        var response2Fail = false
        _isSendingRequest.value = true
        Log.d("ingredients",_selectedIngredients.value.toString())
        if (_selectedIngredients.value.isEmpty()) {
            _isSendingRequest.value = false
            return mutableListOf()
        }
        val request = StringBuilder().apply {
            append(_recipeStart)
            append(" ")
            append(_inputStart)
            append(" ")
            append(_selectedIngredients.value.joinToString(" $_nextInput "))
            append(" $_inputEnd")
        }.toString()
        Log.d("request",request)
        val (response1, response2) = recipeGenerationClient.generateRecipes(request)
        val recipesSuggestions = mutableListOf<Recipe>()
        response1.onSuccess {
            recipesSuggestions.add(parseResponse(response1.toString()))
        }
        response1.onError {
            response1Fail = true
        }
        response2.onSuccess {
            recipesSuggestions.add(parseResponse(response2.toString()))
            _bothFailed.value = false
        }
        response2.onError {
            response2Fail = true
        }

        if(response1Fail && response2Fail)
            _bothFailed.value = true

        _isSendingRequest.value = false
        return recipesSuggestions
    }
    fun decideModel(model:String):String{
        when(model){
            "GPT-2"->return "gpt"
            "Llama"->return "llama"
            else->return ""

        }
    }
    suspend fun parseResponse(response:String):Recipe{
        Log.d("response",response)
        var responseHalf= response.split(_ingredientStart).last()
        val ingredientList=responseHalf.split(_nextIngredient).toMutableList()

        responseHalf=ingredientList.last()
        ingredientList.removeAt(ingredientList.lastIndex)
        ingredientList.add(responseHalf.split(_ingredientEnd).first())
        responseHalf=responseHalf.split(_instructionStart).last()
        val instructionList=responseHalf.split(_nextInstruction).toMutableList()
        responseHalf=instructionList.last()
        instructionList.removeAt(instructionList.lastIndex)
        instructionList.add(responseHalf.split(_instructionEnd).first())
        responseHalf=responseHalf.split(_titleStart).last()
        val title=responseHalf.split(_titleEnd).first()

        val directionsText = instructionList.joinToString(" ")
        val description = try {
            Log.d("RecipeGenVM", "Starting description fetch for $title")
            recipeImageDescriptionClient.generateRecipeDescriptionGemini(title, directionsText)
                ?: "No description available"
        } catch (e: Exception) {
            Log.e("RecipeGenVM", "Description fetch failed", e)
            "Description unavailable: ${e.message}"
        }
        
        val imageUrl = try {
            recipeImageDescriptionClient.fetchImageUrlFromEdamam(title)
                ?: "https://via.placeholder.com/300"
        } catch (e: Exception) {
            Log.e("RecipeGenVM", "Image fetch failed", e)
            "https://via.placeholder.com/300"
        }

        val recipe=Recipe(
            title = title,
            description = "description",
            imageUrl = imageUrl,
            difficulty = "difficulty",
            time = "time",
            ingredients = ingredientList,
            directions = instructionList
        )
       return recipe
    }





}