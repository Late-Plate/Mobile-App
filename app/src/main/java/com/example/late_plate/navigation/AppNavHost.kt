package com.example.late_plate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import android.content.Context
import android.util.Log
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.screens.FABState
import com.example.late_plate.ui.screens.assistant.RecipeAssistant


import com.example.late_plate.ui.screens.login_signup.LoginScreen
import com.example.late_plate.ui.screens.home.HomeScreen
import com.example.late_plate.ui.screens.inventory.InventoryScreen

import com.example.late_plate.ui.screens.login_signup.ForgotPasswordScreen
import com.example.late_plate.ui.screens.login_signup.SignupScreen
import com.example.late_plate.ui.screens.recipe.RecipeScreen
import com.example.late_plate.viewModel.IngredientsViewModel
import com.example.late_plate.viewModel.InventoryViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    applicationContext: Context,
    recipes: List<Recipe>,
    fabState: FABState,
    ingredientsViewModel: IngredientsViewModel,
    inventoryViewModel: InventoryViewModel,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Signup.route) {
            SignupScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            Log.d("Home", recipes.toString())
            HomeScreen(
                data = recipes,
                modifier = Modifier,
                fabState = fabState,
                navController = navController
            )
        }

        composable(Screen.SelectedRecipe.route) {
            RecipeScreen(
                recipe = dummyRecipes.first(),
                modifier = Modifier,
                inventoryViewModel = inventoryViewModel,
                fabState =fabState,
                navController = navController
            )
        }

        composable(Screen.ForgotPass.route) {
            ForgotPasswordScreen(navController = navController)
        }

        composable(Screen.Inventory.route){
            InventoryScreen(
                inventoryViewModel,
                pagerState = pagerState,
                onEdit = { newVal -> ingredientsViewModel.getMatchingIngredients(newVal) },
                modifier = Modifier,
                fabState = fabState
            )
        }

        composable(Screen.RecipeAssistant.route){
            RecipeAssistant(
                navController = navController,
                recipe = dummyRecipes.first(),
                onConfirmation = { ingredients ->
                    inventoryViewModel.removeIngredientsFromInventory(ingredients)
                },
                modifier = Modifier
            )
        }
    }
}
