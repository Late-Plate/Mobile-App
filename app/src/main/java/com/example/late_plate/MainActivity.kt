package com.example.late_plate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.components.CustomFloatingActionButton
import com.example.late_plate.ui.screens.home.HomeScreen
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
                Scaffold(
                    modifier = Modifier.fillMaxSize() ,
                    bottomBar = { CustomBottomNavigationBar(modifier = Modifier) },
                    ) {
                    innerPadding ->
                    RecipeScreen(modifier = Modifier.padding(innerPadding), recipe = dummyRecipes.first())
                }
            }
        }
    }
}

