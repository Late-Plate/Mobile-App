package com.example.late_plate.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.late_plate.dummy.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipesSuggestionViewModel @Inject constructor() : ViewModel(){
    var recipes by mutableStateOf<List<Recipe>>(emptyList())
}