package com.example.late_plate

import android.os.Bundle
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
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.network.RecipeGenerationClient
import com.example.late_plate.network.createHttpClient
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.screens.home.App
import com.example.late_plate.ui.screens.home.HomeScreen
import com.example.late_plate.ui.screens.recipe_generation.RecipeGenerationScreen
import com.example.late_plate.ui.theme.Late_plateTheme
import com.example.late_plate.viewModel.IngredientsViewModel
import io.ktor.client.engine.okhttp.OkHttp

class MainActivity : ComponentActivity() {
    private val ingredientsViewModel: IngredientsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isLoading by mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition{isLoading}
        enableEdgeToEdge()
        setContent {
            val ingredients by ingredientsViewModel.ingredientsList.collectAsState()

            LaunchedEffect(ingredients) {
                if (ingredients.isNotEmpty()) isLoading = false
            }
            Late_plateTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize() ,
                    bottomBar = { CustomBottomNavigationBar(modifier = Modifier) },
                    ) {
                    innerPadding ->
                   // RecipeGenerationScreen()
                    HomeScreen(modifier = Modifier.padding(innerPadding), data = dummyRecipes)

                }
            }
        }
    }

}

