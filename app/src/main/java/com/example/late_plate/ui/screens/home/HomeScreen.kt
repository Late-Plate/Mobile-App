package com.example.late_plate.ui.screens.home
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.RecipeCard
import com.example.late_plate.ui.components.RecommendationCard

@Composable
fun HomeScreen(modifier: Modifier = Modifier,data:List<Recipe>) {
    Column ( modifier=modifier.statusBarsPadding().padding(horizontal = 16.dp)){
        RecipeCard(recipe =  data.first(), onClick = {})
        Spacer(modifier=Modifier.height(24.dp))
        RecommendationCard(recipe = data.last(), onClick = {})

    }

}