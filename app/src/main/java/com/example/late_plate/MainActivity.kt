package com.example.late_plate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.late_plate.data.InventoryDataStore
import com.example.late_plate.ui.screens.MainScreensContainer
import com.example.late_plate.ui.theme.Late_plateTheme
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.InventoryViewModel
import com.example.late_plate.viewModel.RecommendationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val ingredientsViewModel: IngredientsViewModel by viewModels()
    private val recommendationViewModel: RecommendationViewModel by viewModels()
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isLoading by mutableStateOf(true)

        installSplashScreen().setKeepOnScreenCondition { isLoading }
        enableEdgeToEdge()

        setContent {
            val ingredients by ingredientsViewModel.ingredientsList.collectAsState()
            val recipes = recommendationViewModel.getRecipesBlocking(5215572)

            LaunchedEffect(ingredients) {
                if (ingredients.isNotEmpty()) isLoading = false
            }
            val dataStore = InventoryDataStore(this)

            Late_plateTheme {
                MainScreensContainer(ingredientsViewModel,recommendationViewModel,inventoryViewModel,ingredients,recipes,applicationContext)
            }
        }
    }
}

