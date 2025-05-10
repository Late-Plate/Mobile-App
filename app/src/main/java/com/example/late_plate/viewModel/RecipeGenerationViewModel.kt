package com.example.late_plate.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.network.RecipeGenerationClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RecipeGenerationViewModel @Inject constructor(val recipeGenerationClient: RecipeGenerationClient) : ViewModel() {
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
    private val _recipeState = MutableStateFlow<Recipe?>(null)
    val recipeState: StateFlow<Recipe?> = _recipeState.asStateFlow()
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
        _recipeState.value=null
    }
    fun getResponse(model:String){
        viewModelScope.launch {
            _recipeState.value=parseList(model)
        }
    }
    suspend fun parseList(model: String): Recipe? {
        Log.d("ingredients",_selectedIngredients.value.toString())
        if (_selectedIngredients.value.isEmpty()) {
            return null
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
        val response = recipeGenerationClient.generateRecipe(request, decideModel(model))
        return parseResponse(response.toString())
    }
    fun decideModel(model:String):String{
        when(model){
            "GPT-2"->return "gpt"
            "Llama"->return "llama"
            else->return ""

        }
    }
    fun parseResponse(response:String):Recipe{
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
        val recipe=Recipe(
            title = title,
            description = "description",
            imageUrl = "",
            difficulty = "difficulty",
            time = "time",
            ingredients = ingredientList,
            directions = instructionList
        )
       return recipe
    }





}