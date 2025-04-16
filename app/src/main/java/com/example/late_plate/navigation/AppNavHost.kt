package com.example.late_plate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import android.content.Context
import android.util.Log
import com.example.late_plate.FABState
import com.example.late_plate.dummy.Recipe

import com.example.late_plate.ui.screens.login_signup.LoginScreen
import com.example.late_plate.ui.screens.home.HomeScreen

import com.example.late_plate.ui.screens.login_signup.ForgotPasswordScreen
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
    modifier: Modifier = Modifier // <-- add this
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            Log.d("Home", recipes.toString())
            HomeScreen(
                data = recipes
            )
        }

        composable(Screen.ForgotPass.route) {
            ForgotPasswordScreen(navController = navController)
        }
    }
}
