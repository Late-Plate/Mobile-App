package com.example.late_plate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun FilterationBar(){
    val ingredientsList= listOf("flour","chicken","meat")
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)

    ){
        IngredientChip(text = "All", isSelected = true){

        }

        ingredientsList.forEach { ingredient ->
            IngredientChip(text = ingredient, isSelected = false){

            }
        }
    }
}

