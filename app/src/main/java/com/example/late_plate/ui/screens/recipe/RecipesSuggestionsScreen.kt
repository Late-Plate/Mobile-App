package com.example.late_plate.ui.screens.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.RecommendationCard
import com.example.late_plate.ui.screens.FABState
import com.example.late_plate.ui.screens.HomeRecipeRoute

@Composable
fun RecipesSuggestionsScreen(
    modifier: Modifier,
    recipes: List<Recipe>,
    fabState: FABState,
    navController: NavHostController
){

    LazyColumn(
        modifier = modifier.padding(top= 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(recipes){ index, recipe->
            Text(
                text = "Suggestion ${index + 1}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 18.dp),
                fontSize = 20.sp
            )
            Spacer(Modifier.height(16.dp))
            RecommendationCard(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp),
                recipe = recipe,
                onClick = { navController.navigate(HomeRecipeRoute(recipe) )}
            )

        }
    }

}
