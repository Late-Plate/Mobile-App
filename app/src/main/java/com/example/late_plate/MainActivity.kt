package com.example.late_plate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.late_plate.data.InventoryDataStore
import com.example.late_plate.ui.screens.MainScreensContainer
import com.example.late_plate.ui.theme.Late_plateTheme
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.InventoryViewModel
import com.example.late_plate.viewModel.RecipesSuggestionViewModel
import com.example.late_plate.viewModel.RecommendationViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val ingredientsViewModel: IngredientsViewModel by viewModels()
    private val recommendationViewModel: RecommendationViewModel by viewModels()
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private val recipeSuggestionsViewModel: RecipesSuggestionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        var isLoading by mutableStateOf(true)

        installSplashScreen().setKeepOnScreenCondition { isLoading }
        enableEdgeToEdge()

        setContent {
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { 2 }
            )

            val ingredients by ingredientsViewModel.ingredientsList.collectAsState()
            val recipes = recommendationViewModel.getRecipesBlocking(5215572)

            LaunchedEffect(ingredients) {
                if (ingredients.isNotEmpty()) isLoading = false
            }
            val dataStore = InventoryDataStore(this)

            Late_plateTheme {
                MainScreensContainer(
                    ingredientsViewModel,
                    recommendationViewModel,
                    inventoryViewModel,
                    recipeSuggestionsViewModel,
                    ingredients,
                    applicationContext,
                    pagerState
                )
            }
        }
    }
}

