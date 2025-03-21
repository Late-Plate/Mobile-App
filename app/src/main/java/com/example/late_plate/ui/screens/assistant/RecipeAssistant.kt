@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.late_plate.ui.screens.assistant

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.late_plate.R
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.OnlineImageCard
import com.example.late_plate.view_model.RecipeAssistantViewModel
import com.example.late_plate.view_model.TimerState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeAssistantScreen(modifier: Modifier, recipe: Recipe){

    val assistantViewModel: RecipeAssistantViewModel = viewModel()

    val stepIndex by assistantViewModel.stepIndex

    val totalSteps = recipe.steps.size

    val assistantIcons = listOf(
        R.drawable.mix,
        R.drawable.temperature
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    recipe.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { }) {
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
        ){
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Step ${stepIndex + 1} of ${totalSteps}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            text = recipe.steps[stepIndex],
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        if (recipe.steps[stepIndex].contains("minute", ignoreCase = true) ||
                        recipe.steps[stepIndex].contains("hour", ignoreCase = true)) {

                            val totalMilliseconds =  extractTime(recipe.steps[stepIndex])
                            val timerState = assistantViewModel.getOrCreateTimer(stepIndex, totalMilliseconds)
                            CountdownTimerWithProgress(
                                timerState = timerState,
                                onStart = { assistantViewModel.startTimer(stepIndex) },
                                onReset = { assistantViewModel.resetTimer(stepIndex) }
                            )

                    }


                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            if (stepIndex > 0) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        assistantViewModel.goToPreviousStep()
                                    },
                                    imageVector = Icons.Filled.ArrowBack,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            if (recipe.steps[stepIndex].contains("preheat", ignoreCase = true)) {
                                Icon(
                                    painter = painterResource(assistantIcons[1]),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }


                        }


                    }
                }
                Icon(
                    modifier = Modifier.clickable {
                        assistantViewModel.goToNextStep(totalSteps)
                    }.padding(top = 16.dp),
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }


    }
}

@Preview
@Composable
fun PreviewAssistantScreen(){
    RecipeAssistantScreen(Modifier, dummyRecipes[0])
}

fun extractTime(step: String): Long{
    val words = step.split(" ", "-") // Split into words
    var totalMilliseconds: Long = 0

    words.forEachIndexed { index, word ->
        when {
            word.toLowerCase().contains("hour", ignoreCase = true) && index > 0 -> {
                val hours = words[index - 1].toLongOrNull() // Get the number before "hour"
                Log.d("HOURS!", "Contains Hours")
                Log.d("HOURS!", "Extracted hours: $hours")
                if (hours != null) {
                    totalMilliseconds += hours * 60 * 60 * 1000
                }
            }
            word.toLowerCase().contains("minute", ignoreCase = true) && index > 0 -> {
                val minutes = words[index - 1].toLongOrNull() // Get the number before "minute"
                Log.d("MINUTES!", "Contains Minutes")
                Log.d("MINUTES!", "Extracted minutes: $minutes")
                if (minutes != null) {
                    totalMilliseconds += minutes * 60 * 1000
                }
            }
        }
    }

    Log.d("TIMER Extracted", "Time: $totalMilliseconds ms")

    return totalMilliseconds

}

@Composable
fun CountdownTimerWithProgress(
    timerState: TimerState,
    onStart: () -> Unit,
    onReset: () -> Unit
) {
    val timeLeft by timerState.timeLeft
    val isRunning by timerState.isRunning

    val hours = (timeLeft / 1000) / 3600
    val minutes = ((timeLeft / 1000) % 3600) / 60
    val seconds = (timeLeft / 1000) % 60
    val progress = if (timerState.totalTime > 0) timeLeft.toFloat() / timerState.totalTime.toFloat() else 0f

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
