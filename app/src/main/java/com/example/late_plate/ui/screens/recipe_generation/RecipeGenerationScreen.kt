package com.example.late_plate.ui.screens.recipe_generation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.CustomTextField
import com.example.late_plate.ui.components.ExpandableSelectionCard
import com.example.late_plate.ui.components.IngredientChip
import com.example.late_plate.ui.components.SelectedItem
import com.example.late_plate.ui.screens.FABState
import com.example.late_plate.ui.screens.GenRecipeRoute
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.RecipeGenerationViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeGenerationScreen(
    modifier: Modifier = Modifier,
    ingredientsViewModel: IngredientsViewModel,
    recipeGenerationViewModel: RecipeGenerationViewModel = hiltViewModel(),
    fabState: FABState,
    navController: NavController
) {
    var selectedModel by remember { mutableStateOf("Llama") }
    val models = listOf("GPT-2","Llama")
    val defaultIngredientsList = listOf("Rice", "Chicken", "Beans", "Salt")
    recipeGenerationViewModel.setIngredients(defaultIngredientsList)
    var searchText by remember { mutableStateOf("") }
    val selectedIngredients = remember { mutableStateListOf<String>() }
    val recipe by recipeGenerationViewModel.recipeState.collectAsState()
    LaunchedEffect(recipe) {
        recipe?.let {
            navController.navigate(GenRecipeRoute(it))
        }
    }
    fabState.changeFAB(Icons.Rounded.Bolt, newOnClick = {
      recipeGenerationViewModel.getResponse(selectedModel)
    })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .statusBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CustomCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(), contentPadding = 0
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    "Got Ingredients? Get Inspired!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,

                    )
                Text(
                    "\"Unlock unique AI generated recipes with only one click\"",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(modifier = Modifier) {
                    item { Spacer(modifier=Modifier.width(16.dp)) }

                    items(recipeGenerationViewModel.ingredients.value) { item ->
                        IngredientChip(modifier = Modifier.padding(4.dp), item, false) {
                            if (it !in selectedIngredients) {
                                selectedIngredients.add(it)
                                recipeGenerationViewModel.setSelectedIngredients(selectedIngredients)
                                Log.d("selected",selectedIngredients.toString())
                            }
                            searchText=""
                        }
                    }
                    item { Spacer(modifier=Modifier.width(16.dp)) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    placeholder = "Search ingredients",
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        if (it.isNotEmpty()) {
                            recipeGenerationViewModel.setIngredients(
                                ingredientsViewModel.getMatchingIngredients(
                                    it
                                )
                            )
                        } else {
                            recipeGenerationViewModel.setIngredients(defaultIngredientsList)
                        }
                    })

                Spacer(modifier = Modifier.height(8.dp))


            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        ExpandableSelectionCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            options = models,
            selectedOption = selectedModel,
            onOptionSelected = { selectedModel = it },
            label = "AI Model"
        )

        Spacer(modifier = Modifier.height(16.dp))
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.verticalScroll(scrollState).fillMaxSize()) {
            if (selectedIngredients.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedIngredients.forEach { item ->
                        SelectedItem(item, onClick = {
                            selectedIngredients.remove(it)
                        })
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(86.dp))
                }
            } else {
                Text(
                    "selected ingredients will show up here!",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally,)
                )
            }
        }
    }
}
