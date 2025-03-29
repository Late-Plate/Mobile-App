package com.example.late_plate.ui.screens.recipe_generation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.R
import com.example.late_plate.ui.components.IngredientChip
import com.example.late_plate.ui.components.SelectedItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeGenerationScreen() {
    val defaultIngredientsList = listOf("Rice", "Chicken", "Beans", "Salt")
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
                IconButton(onClick = { }) {
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
                    items(defaultIngredientsList) { item ->
                        IngredientChip(item, false) {
                            if (it !in selectedIngredients) {
                                selectedIngredients.add(it)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Field (Placeholder)
                TextField(
                    value = "Look for ingredients",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Ingredients List
        LazyColumn {
            items(selectedIngredients) { item ->
                SelectedItem(item)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeGenerationScreenPreview() {
    RecipeGenerationScreen()
}
