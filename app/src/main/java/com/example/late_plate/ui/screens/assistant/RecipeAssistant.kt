@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.late_plate.ui.screens.assistant

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.late_plate.R
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.navigation.Screen
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.OnlineImageCard
import com.example.late_plate.viewModel.AlarmNotificationHelper
import com.example.late_plate.viewModel.RecipeAssistantViewModel
import com.example.late_plate.viewModel.TimerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeAssistantScreen(
    navController: NavController,
    recipe: Recipe,
    onConfirmation: (List<String>) -> Unit,
) {
    val assistantViewModel: RecipeAssistantViewModel = viewModel()
    LaunchedEffect(recipe) {
        assistantViewModel.loadRecipe(recipe)
    }
    val context = LocalContext.current
    val alarmHelper = remember { AlarmNotificationHelper(context) }

    val isFinished by assistantViewModel.isFinished

    val goToRecipe = remember { mutableStateOf(false) }

    if (goToRecipe.value) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.SelectedRecipe.route) {
                popUpTo(Screen.RecipeAssistant.route) { inclusive = true }
            }
            goToRecipe.value = false // reset state if needed
        }
    }


    LaunchedEffect(Unit) {
        assistantViewModel.alarmEvents.collect { key ->
            alarmHelper.showTimerFinishedNotification(key)
        }
    }

    var stepIndex = assistantViewModel.stepIndex.value

    val currentRecipeTimers = assistantViewModel.allTimerStates
        .filterKeys { it.recipeName == recipe.title }



    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    recipe.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* Handle back navigation */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            )
        )

        OnlineImageCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
                .padding(16.dp),
            imageUrl = recipe.imageUrl
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CustomCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Step ${stepIndex + 1} of ${recipe.directions.size}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            text = recipe.directions[stepIndex],
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        val timerKey = assistantViewModel.recipeTimerKey.value
                        assistantViewModel.allTimerStates[timerKey]?.let { timerState ->
                            if (timerState.totalTime > 0) {
                                CountdownTimerWithProgress(
                                    timerState = timerState,
                                    onStart = { assistantViewModel.startTimer(recipe.title, stepIndex) },
                                    onReset = {
                                        assistantViewModel.resetTimer(recipe.title, stepIndex)
                                        if (timerKey != null) {
                                            assistantViewModel.dismissAlarm(timerKey)
                                        }
                                    }
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (stepIndex > 0) {
                                Icon(
                                    modifier = Modifier
                                        .clickable {
                                            assistantViewModel.goToPreviousStep()
                                        }
                                        .size(42.dp),
                                    imageVector = Icons.Filled.ArrowBack,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            if (recipe.directions[stepIndex].lowercase().contains("oven")) {
                                Icon(
                                    painter = painterResource(R.drawable.temperature),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                            else if(recipe.directions[stepIndex].lowercase().contains("combine") ||
                                recipe.directions[stepIndex].lowercase().contains("mix")){
                                Icon(
                                    painter = painterResource(R.drawable.mix),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }

                Icon(
                    modifier = Modifier
                        .clickable {
                            assistantViewModel.goToNextStep(recipe.directions.size)
                        }
                        .padding(top = 16.dp),
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        assistantViewModel.finishedTimers.values.forEach { (key, state)->
            TimerAlarmDialog(
                recipeName = key.recipeName,
                stepIndex = key.stepIndex,
                onDismiss = { assistantViewModel.dismissAlarm(key) }
            )
        }
    }
    if(isFinished){
        // call alert
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            RecipeFinishedPopUp(
                onConfirmation = {
                    onConfirmation(recipe.ingredients)
                    goToRecipe.value = true
                    assistantViewModel.setIsFinished(false)

                },
                onCancel = { assistantViewModel.setIsFinished(false) }
            )
        }
    }

}

@Composable
fun CountdownTimerWithProgress(
    timerState: TimerState,
    onStart: () -> Unit,
    onReset: () -> Unit
) {
    val hours = (timerState.timeLeft / 1000) / 3600
    val minutes = ((timerState.timeLeft / 1000) % 3600) / 60
    val seconds = (timerState.timeLeft / 1000) % 60
    val progress = if (timerState.totalTime > 0) {
        timerState.timeLeft.toFloat() / timerState.totalTime.toFloat()
    } else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
            fontSize = 36.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = colorResource(R.color.cancel_btn_color)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.height(ButtonDefaults.MinHeight)
            ) {
                Text("Start")
            }
            Button(
                onClick = onReset,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.height(ButtonDefaults.MinHeight)
            ) {
                Text("Reset")
            }
        }
    }
}

@Preview
@Composable
fun PreviewAssistantScreen() {
    RecipeFinishedPopUp(onConfirmation = {}) { }
}

@Composable
fun RecipeFinishedPopUp(
    onConfirmation: ()-> Unit,
    onCancel: ()-> Unit
){
    Card(
        modifier= Modifier
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .border(
                width = 1.dp, // Border thickness
                color = MaterialTheme.colorScheme.background, // Border color
                shape = RoundedCornerShape(16.dp) // Must match shadow shape
            )
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.plate_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(130.dp)
                        .padding(bottom = 4.dp)
                )
                Text(
                    text = "Ready to enjoy your delicious creation?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {onConfirmation()},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Absolutely"
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {onCancel()},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.cancel_btn_color),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Not yet"
                    )
                }
            }
        }

    }

}