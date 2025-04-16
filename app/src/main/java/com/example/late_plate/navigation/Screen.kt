package com.example.late_plate.navigation

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Inventory: Screen("inventory")
    object Login: Screen("login")
    object Signup: Screen("signup")
    object ForgotPass: Screen("forgot_pass")
}