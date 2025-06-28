package com.example.late_plate

import android.os.Bundle
import android.util.Log
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
import com.example.late_plate.viewModel.RecipeCatalogViewModel
import com.example.late_plate.viewModel.RecommendationViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val ingredientsViewModel: IngredientsViewModel by viewModels()
    private val recommendationViewModel: RecommendationViewModel by viewModels()
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private val recipeSuggestionsViewModel: RecipesSuggestionViewModel by viewModels()

    private val recipeCatalogViewModel: RecipeCatalogViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
//            if (throwable != null) {
//                Log.e("CRASH_HANDLER", "Uncaught error in thread: $thread", throwable)
//            } else {
//                Log.e("CRASH_HANDLER", "Uncaught error in thread: $thread - throwable was null!")
//            }
//        }


        // Initialize Firebase with comprehensive error handling
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MainActivity", "Firebase initialized successfully")
            } else {
                Log.d("MainActivity", "Firebase already initialized")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Firebase initialization failed", e)
            // Continue without Firebase - don't crash the app
        }
        var isLoading by mutableStateOf(true)

        installSplashScreen().setKeepOnScreenCondition { isLoading }
        enableEdgeToEdge()

        setContent {
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { 2 }
            )
            LaunchedEffect(Unit) {
                try {
                    recommendationViewModel.getRecipesBlocking(5215572)
                    recipeCatalogViewModel.getRecipes(0, 10)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error during initial recipe loading", e)
                } finally {
                    isLoading = false // Ensure this is always set, even on failure
                }
            }
//            val recipes = recommendationViewModel.getRecipesBlocking(5215572)
            Log.d("after retrieving recipes","...........")
//            recipeCatalogViewModel.getRecipes(0,10);
            Log.d("after recipe catalog","...........")
            val catalogRecipes = recipeCatalogViewModel.recipes.collectAsState()
            Log.d("after recipe catalog collect state","...........")

            val dataStore = InventoryDataStore(this)

            Late_plateTheme {
                MainScreensContainer(
                    ingredientsViewModel,
                    recommendationViewModel,
                    inventoryViewModel,
                    recipeSuggestionsViewModel,
                    applicationContext,
                    pagerState,
                    recipeCatalogViewModel
                )
            }
        }
    }
}

