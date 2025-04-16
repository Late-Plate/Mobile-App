package com.example.late_plate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.late_plate.data.InventoryDataStore
import com.example.late_plate.navigation.AppNavHost
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.theme.Late_plateTheme
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.InventoryPopUpState
import com.example.late_plate.viewModel.InventoryViewModel
import com.example.late_plate.viewModel.RecommendationViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val ingredientsViewModel: IngredientsViewModel by viewModels()
    private val recommendationViewModel: RecommendationViewModel by viewModels()
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        var isLoading by mutableStateOf(true)

        installSplashScreen().setKeepOnScreenCondition { isLoading }
        enableEdgeToEdge()

        setContent {
            val ingredients by ingredientsViewModel.ingredientsList.collectAsState()
            val recipes = recommendationViewModel.getRecipesBlocking(5215572)
            Log.d("recipes", recipes.toString())
            val navController = rememberNavController()
            LaunchedEffect(ingredients) {
                if (ingredients.isNotEmpty()) isLoading = false
            }

            val fabState = rememberFABState(Icons.Outlined.Person) {
                inventoryViewModel.addOrUpdate = InventoryPopUpState.ADD
                Log.d("ADD", inventoryViewModel.addOrUpdate.toString())
                inventoryViewModel.openDialog()
            }

            val dataStore = InventoryDataStore(this)

            Late_plateTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        CustomBottomNavigationBar(fabState)
                    }
                ){ innerPadding ->
                    AppNavHost(
                        navController = navController,
                        applicationContext = this.applicationContext,
                        recipes = recipes,
                        fabState = fabState,
                        ingredientsViewModel = ingredientsViewModel,
                        inventoryViewModel = inventoryViewModel,
                        modifier = Modifier.padding(innerPadding) // Add padding here if needed
                    )
//                    LoginScreen(modifier = Modifier.padding(innerPadding), navController)

                    // Example: You can switch screens here depending on state/navigation
                    // Uncomment the screen you want to show:

//                     IngredientDetectionScreen(
//                         modifier = Modifier.padding(innerPadding),
//                         context = applicationContext,
//                         fabState = fabState
//                     )

//                    HomeScreen(
//                        modifier = Modifier.padding(innerPadding),
//                        data = recipes
//                    )

                    // RecipeGenerationScreen(
                    //     ingredientsViewModel,
                    //     modifier = Modifier.padding(innerPadding)
                    // )

                    // InventoryScreen(
                    //     inventoryViewModel,
                    //     modifier = Modifier.padding(innerPadding),
                    //     onEdit = { newVal ->
                    //         ingredientsViewModel.getMatchingIngredients(newVal)
                    //     }
                    // )

                    // RecipeAssistantScreen(
                    //     modifier = Modifier.padding(innerPadding),
                    //     recipe = dummyRecipes.first()
                    // )
                }
            }
        }
    }
}

class FABState(
    icon: ImageVector,
    onClick: () -> Unit
) {
    val icon: MutableState<ImageVector> = mutableStateOf(icon)
    val onClick: MutableState<() -> Unit> = mutableStateOf(onClick)

    fun changeFAB(newIcon: ImageVector, newOnClick: () -> Unit) {
        icon.value = newIcon
        onClick.value = newOnClick
    }
}

@Composable
fun rememberFABState(icon: ImageVector, onClick: () -> Unit): FABState {
    return remember { FABState(icon, onClick) }
}
