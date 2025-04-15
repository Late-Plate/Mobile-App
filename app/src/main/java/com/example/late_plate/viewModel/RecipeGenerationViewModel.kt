package com.example.late_plate.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.rememberTransition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.network.RecipeGenerationClient
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val _recipeEnd="<RECIPE_END>"
    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
    val ingredients=_ingredients.asStateFlow()
    fun setIngredients(ingredients:List<String>){
        _ingredients.value=ingredients
    }
    fun parseList(){
        if(_ingredients.value.isEmpty()){
         return
        }
        val request=StringBuilder();
        request.append(_recipeStart)
        request.append(_inputStart)
        val ingredientsStr=_ingredients.value.joinToString(_nextInput)
        request.append(ingredientsStr)
        request.append(_inputEnd)
        viewModelScope.launch {
            val res=recipeGenerationClient.generateRecipe(request.toString()).toString()
            parseResponse(res)
        }
    }
    fun parseResponse(response:String){
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
            steps = instructionList
        )
        Log.d("recipe",recipe.toString())
    }





}