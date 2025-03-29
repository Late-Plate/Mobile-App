package com.example.late_plate

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.late_plate.data.InventoryDataStore
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.components.CustomBottomNavigationBar
import com.example.late_plate.ui.components.CustomFloatingActionButton
import com.example.late_plate.ui.screens.assistant.RecipeAssistantScreen
import com.example.late_plate.ui.screens.home.HomeScreen
import com.example.late_plate.ui.screens.inventory.CustomInventoryPopup
import com.example.late_plate.ui.screens.inventory.InventoryScreen
import com.example.late_plate.ui.screens.login.LoginScreen
import com.example.late_plate.ui.screens.recipe.RecipeScreen
import com.example.late_plate.ui.theme.Late_plateTheme
import com.example.late_plate.view_model.InventoryPopUpState
import com.example.late_plate.view_model.InventoryViewModel
import com.example.late_plate.view_model.InventoryViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val dataStore = InventoryDataStore(this)
            val viewModelFactory = InventoryViewModelFactory(dataStore)
            val inventoryViewModel: InventoryViewModel = viewModel(factory = viewModelFactory)

            Late_plateTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize() ,
                    bottomBar = { CustomBottomNavigationBar(modifier = Modifier, onClick = {
                        inventoryViewModel.addOrUpdate = InventoryPopUpState.ADD
                        Log.d("ADD", inventoryViewModel.addOrUpdate.toString())
                        inventoryViewModel.openDialog()} )},
                    ) {
                    innerPadding ->
//                    RecipeAssistantScreen(modifier = Modifier.padding(innerPadding), dummyRecipes.first())
                      InventoryScreen(inventoryViewModel, modifier = Modifier.padding(innerPadding))
//                    RecipeScreen(modifier = Modifier.padding(innerPadding), recipe = dummyRecipes.first())

                }



            }
        }
    }
}

