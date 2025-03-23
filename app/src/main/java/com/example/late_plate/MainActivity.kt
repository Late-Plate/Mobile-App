package com.example.late_plate

import IngredientDetectionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.screens.home.HomeScreen

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
                   IngredientDetectionScreen(context=applicationContext,modifier=Modifier.padding(innerPadding))
                }
            }
        }
    }
}

