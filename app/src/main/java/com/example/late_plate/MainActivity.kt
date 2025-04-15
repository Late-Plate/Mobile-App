package com.example.late_plate

import com.example.late_plate.ui.screens.ingredients_detection.IngredientDetectionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.screens.login.LoginScreen
import com.example.late_plate.ui.screens.recipe.RecipeScreen

import com.example.late_plate.ui.theme.Late_plateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            Late_plateTheme {
                val fabState = rememberFABState(Icons.Outlined.Person, {})
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        CustomBottomNavigationBar(fabState)
                    },
                ) { innerPadding ->
                    IngredientDetectionScreen(
                        modifier = Modifier.padding(innerPadding),
                        context = applicationContext
                        , fabState = fabState
                    )
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
