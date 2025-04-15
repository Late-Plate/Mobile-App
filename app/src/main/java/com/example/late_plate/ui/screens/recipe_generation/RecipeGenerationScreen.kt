package com.example.late_plate.ui.screens.recipe_generation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.late_plate.R
import com.example.late_plate.ui.components.IngredientChip
import com.example.late_plate.ui.components.SelectedItem
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.RecipeGenerationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeGenerationScreen(ingredientsViewModel: IngredientsViewModel,
                           modifier: Modifier = Modifier,
                           recipeGenerationViewModel:RecipeGenerationViewModel =hiltViewModel()) {
    val defaultIngredientsList = listOf("Rice", "Chicken", "Beans", "Salt")
    recipeGenerationViewModel.setIngredients(defaultIngredientsList)
    var searchText by remember { mutableStateOf("") }
    val selectedIngredients = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Recipe Generation", color = MaterialTheme.colorScheme.onPrimary) },
            navigationIcon = {
                IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ingredients Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.what_ingredients_do_you_have),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )

                // Ingredient Chips
                LazyRow(modifier = Modifier.padding(8.dp)) {

                    items(recipeGenerationViewModel.ingredients.value) { item ->
                        IngredientChip(item, false) {
                            if (it !in selectedIngredients) {
                                selectedIngredients.add(it)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Field
                TextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        if (it.isNotEmpty()) {
                           recipeGenerationViewModel.setIngredients( ingredientsViewModel.getMatchingIngredients(it))
                        } else {
                            recipeGenerationViewModel.setIngredients(defaultIngredientsList)
                        }
                    },
                    placeholder = { Text("Look for ingredients") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { recipeGenerationViewModel.parseList() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Generate Recipe")
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Ingredients List
        LazyColumn {
            items(selectedIngredients) { item ->
                SelectedItem(item, onClick ={
                    selectedIngredients.remove(it)
                })
            }
        }
    }
}
