package com.example.late_plate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.late_plate.data.InventoryDataStore
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.network.RecipeGenerationClient
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.screens.home.HomeScreen
import com.example.late_plate.ui.screens.inventory.InventoryScreen

import com.example.late_plate.ui.screens.recipe_generation.RecipeGenerationScreen
import com.example.late_plate.ui.theme.Late_plateTheme
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.InventoryPopUpState
import com.example.late_plate.viewModel.InventoryViewModel
import com.example.late_plate.viewModel.RecommendationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
        private val ingredientsViewModel: IngredientsViewModel by viewModels()
        private val recommendationViewModel: RecommendationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isLoading by mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition{isLoading}
        enableEdgeToEdge()
        setContent {
            val ingredients by ingredientsViewModel.ingredientsList.collectAsState()
            val recipes=recommendationViewModel.getRecipesBlocking(5215572)
            LaunchedEffect(ingredients) {
                if (ingredients.isNotEmpty()) isLoading = false
            }
            val dataStore = InventoryDataStore(this)

            val inventoryViewModel: InventoryViewModel by viewModels()

            Late_plateTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize() ,
                    bottomBar = { CustomBottomNavigationBar(modifier = Modifier, onClick = {
                        inventoryViewModel.addOrUpdate = InventoryPopUpState.ADD
                        Log.d("ADD", inventoryViewModel.addOrUpdate.toString())
                        inventoryViewModel.openDialog()} )},
                    ) {
                    innerPadding ->
                        //RecipeGenerationScreen(ingredientsViewModel, modifier = Modifier.padding(innerPadding))
                        HomeScreen(modifier = Modifier.padding(innerPadding), data = recipes)
                    //RecipeAssistantScreen(modifier = Modifier.padding(innerPadding), dummyRecipes.first())
//                     InventoryScreen(
//                        inventoryViewModel,
//                        modifier = Modifier.padding(innerPadding),
//                        onEdit = {newVal -> ingredientsViewModel.getMatchingIngredients(newVal)}
//                        )
                }
            }
        }
    }
}

