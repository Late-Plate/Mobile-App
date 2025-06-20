package com.example.late_plate.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.screens.home.HomeScreen
import com.example.late_plate.ui.screens.ingredients_detection.IngredientDetectionScreen
import com.example.late_plate.ui.screens.inventory.InventoryScreen
import com.example.late_plate.ui.screens.login_signup.ForgotPasswordScreen
import com.example.late_plate.ui.screens.login_signup.LoginScreen
import com.example.late_plate.ui.screens.login_signup.SignupScreen
import com.example.late_plate.ui.screens.recipe.RecipeScreen
import com.example.late_plate.ui.screens.recipe.RecipesSuggestionsScreen
import com.example.late_plate.ui.screens.recipe_generation.RecipeGenerationScreen
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.InventoryViewModel
import com.example.late_plate.viewModel.RecipeCatalogViewModel
import com.example.late_plate.viewModel.RecipesSuggestionViewModel
import com.example.late_plate.viewModel.RecommendationViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainScreensContainer(
    ingredientsViewModel: IngredientsViewModel,
    recommendationViewModel: RecommendationViewModel,
    inventoryViewModel: InventoryViewModel,
    recipesSuggestionViewModel: RecipesSuggestionViewModel,
    ingredients: List<String>,
    applicationContext: Context,
    pagerState: PagerState,
    recipeCatalogViewModel: RecipeCatalogViewModel
) {
    val fabState = rememberFABState(Icons.Outlined.Person, onClick = {})
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(
        LoginRoute::class.qualifiedName,
        SignupRoute::class.qualifiedName,
        ForgotPasswordRoute::class.qualifiedName
    )


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination !in hideBottomBarRoutes) {
                CustomBottomNavigationBar(fabState, navController)
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = LoginRoute) {
            composable<HomeRoute> {
                val recipes by recommendationViewModel.recipes.collectAsState()
                Log.d("RECIPES", recipes.toString())
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    data = recipes, fabState = fabState
                    ,navController
                )
            }
            composable<LoginRoute>{
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
            composable<SignupRoute>{
                SignupScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
            composable<ForgotPasswordRoute>{
                ForgotPasswordScreen(
                    navController = navController,
                )
            }
            composable<InventoryRoute> {
                InventoryScreen(
                    inventoryViewModel,
                    modifier = Modifier.padding(innerPadding),
                    onEdit = { newVal ->
                        ingredientsViewModel.getMatchingIngredients(newVal)
                    },
                    fabState = fabState,
                    pagerState = pagerState
                )
            }
            composable<RecipeGenerationRoute> {
                RecipeGenerationScreen(
                    modifier = Modifier.padding(innerPadding),
                    recipesSuggestionViewModel = recipesSuggestionViewModel,
                    ingredientsViewModel = ingredientsViewModel, fabState = fabState,
                    navController=navController
                )
            }
            composable<IngredientDetectionRoute> {
                IngredientDetectionScreen(
                    modifier = Modifier.padding(innerPadding),
                    context = applicationContext,
                    fabState = fabState
                )
            }
            composable<HomeRecipeRoute> (typeMap = mapOf(typeOf<Recipe>() to RecipeNavType) ){
                val arg=it.toRoute<HomeRecipeRoute>()
                RecipeScreen(
                    modifier = Modifier.padding(innerPadding),
                    recipe = arg.recipe,
                    fabState = fabState,
                    navController = navController,
                    inventoryViewModel = inventoryViewModel
                )
            }
            composable<RecipesSuggestionsRoute> {
                RecipesSuggestionsScreen(
                    modifier = Modifier.padding(innerPadding),
                    recipes = recipesSuggestionViewModel.recipes,
                    fabState = fabState,
                    navController = navController
                )
            }
            composable<GenRecipeRoute> (typeMap = mapOf(typeOf<Recipe>() to RecipeNavType) ){
                val arg=it.toRoute<GenRecipeRoute>()
                RecipeScreen(
                    modifier = Modifier.padding(innerPadding),
                    recipe = arg.recipe,
                    fabState = fabState,
                    navController = navController,
                    inventoryViewModel = inventoryViewModel
                )
            }
        }

    }
}
object RecipeNavType : NavType<Recipe>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Recipe {
        return Json.decodeFromString(bundle.getString(key)!!)
    }

    override fun serializeAsValue(value: Recipe): String {
        return Uri.encode( Json.encodeToString(value))
    }
    override fun parseValue(value: String): Recipe {
        return Json.decodeFromString(value)
    }


    override fun put(bundle: Bundle, key: String, value: Recipe) {
        bundle.putString(key, Json.encodeToString(value))
    }
}
object RecipeListNavType : NavType<List<Recipe>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<Recipe> {
        val json = bundle.getString(key) ?: return emptyList()
        return Json.decodeFromString(ListSerializer(Recipe.serializer()), json)
    }

    override fun parseValue(value: String): List<Recipe> {
        return Json.decodeFromString(ListSerializer(Recipe.serializer()), value)
    }

    override fun put(bundle: Bundle, key: String, value: List<Recipe>) {
        bundle.putString(key, Json.encodeToString(ListSerializer(Recipe.serializer()), value))
    }
}


class FABState(
    icon: ImageVector,
    onClick: () -> Unit,
    isLoading: Boolean=false
) {
    val isLoading: MutableState<Boolean> = mutableStateOf(isLoading)
    val icon: MutableState<ImageVector> = mutableStateOf(icon)
    val onClick: MutableState<() -> Unit> = mutableStateOf(onClick)

    fun changeFAB(newIcon: ImageVector, newOnClick: () -> Unit) {
        icon.value = newIcon
        onClick.value = newOnClick
    }
    fun loading(isLoading:Boolean=false){
            this.isLoading.value=isLoading
    }

}

@Composable
fun rememberFABState(icon: ImageVector, onClick: () -> Unit): FABState {
    return remember { FABState(icon, onClick) }
}

@Serializable
object HomeRoute

@Serializable
object IngredientDetectionRoute

@Serializable
object RecipeGenerationRoute

@Serializable
object InventoryRoute

@Serializable
object LoginRoute

@Serializable
object SignupRoute

@Serializable
object ForgotPasswordRoute

@Serializable
data class HomeRecipeRoute(val recipe: Recipe)

@Serializable
data class GenRecipeRoute(val recipe: Recipe)

@Serializable
object RecipesSuggestionsRoute

// Example: You can switch screens here depending on state/navigation
// Uncomment the screen you want to show:

//                     IngredientDetectionScreen(
//                         modifier = Modifier.padding(innerPadding),
//                         context = applicationContext,
//                         fabState = fabState
//                     )

//                    HomeScreen(
//                        modifier = Modifier.padding(innerPadding),
//                        data = dummyRecipes
//                        ,fabState=fabState
//                    )

//                     RecipeGenerationScreen(
//                         modifier = Modifier.padding(innerPadding),
//                         ingredientsViewModel
//                         , fabState = fabState
//                     )

//                     InventoryScreen(
//                         inventoryViewModel,
//                         modifier = Modifier.padding(innerPadding),
//                         onEdit = { newVal ->
//                             ingredientsViewModel.getMatchingIngredients(newVal)
//                         }
//                     )

//                     RecipeScreen(
//                         modifier = Modifier.padding(innerPadding),
//                         recipe = dummyRecipes.first(),fabState=fabState
//                     )