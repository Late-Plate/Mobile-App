package com.example.late_plate.ui.screens.assistant

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateLeft
import androidx.compose.material.icons.rounded.Blender
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Cookie
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.SoupKitchen
import androidx.compose.material.icons.rounded.TurnSlightRight
import androidx.compose.material.icons.rounded.Wash
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.viewModel.AlarmNotificationHelper
import com.example.late_plate.viewModel.RecipeAssistantViewModel
import com.example.late_plate.viewModel.TimerState

@Composable
fun RecipeAssistant(modifier: Modifier, recipe: Recipe) {
    val assistantViewModel: RecipeAssistantViewModel = viewModel()
    LaunchedEffect(recipe) {
        assistantViewModel.loadRecipe(recipe)
    }
    val context = LocalContext.current
    val alarmHelper = remember { AlarmNotificationHelper(context) }

    LaunchedEffect(Unit) {
        assistantViewModel.alarmEvents.collect { key ->
            alarmHelper.showTimerFinishedNotification(key)
        }
    }

    val stepIndex = assistantViewModel.stepIndex.value

    val currentRecipeTimers = assistantViewModel.allTimerStates
        .filterKeys { it.recipeName == recipe.title }


    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), contentPadding = 0

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = 16.dp).padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .clickable { assistantViewModel.goToPreviousStep() }
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                            .size(24.dp),
                        tint = if (stepIndex == 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Step ${stepIndex + 1} of ${recipe.directions.size}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .clickable { assistantViewModel.goToNextStep(recipe.directions.size) }
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                            .size(24.dp),
                        tint = if (stepIndex == recipe.directions.size - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                    )
                }
                StepsIcons(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(28.dp),
                    recipe.directions[stepIndex]
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = recipe.directions[stepIndex],
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary,

            )
            val timerKey = assistantViewModel.recipeTimerKey.value
            assistantViewModel.allTimerStates[timerKey]?.let { timerState ->
                if (timerState.totalTime > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
                else{
                    Spacer(modifier=Modifier.height(16.dp))
                }
            }


        }
    }

    assistantViewModel.finishedTimers.values.forEach { (key, state) ->
        TimerAlarmDialog(
            recipeName = key.recipeName,
            stepIndex = key.stepIndex,
            onDismiss = { assistantViewModel.dismissAlarm(key) }
        )
    }
}


@SuppressLint("DefaultLocale")
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp).padding(bottom = 16.dp)
    ) {

        Text(
            text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface,
            StrokeCap.Round,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "reset",
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .clickable(onClick = onReset)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            CustomButton(
                onClick = onStart,
                content = { Text("start timer") })
        }
    }
}


@Composable
fun StepsIcons(modifier: Modifier, direction: String) {
    val lowercasedDirection = direction.lowercase()

    if (lowercasedDirection.contains("oven") || lowercasedDirection.contains("heat")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = "Heat/Oven",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("combine") || lowercasedDirection.contains("mix") || lowercasedDirection.contains(
            "stir"
        )
    ) {
        Icon(
            modifier = modifier,
            imageVector = Icons.AutoMirrored.Outlined.RotateLeft,
            contentDescription = "Combine/Mix/Stir",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("blend") || lowercasedDirection.contains("puree")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.Blender,
            contentDescription = "Blend/Puree",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("bake")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.Cookie,
            contentDescription = "Bake",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("chop") || lowercasedDirection.contains("cut") || lowercasedDirection.contains(
            "dice"
        )
    ) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.ContentCut, // Assuming you import this custom icon
            contentDescription = "Chop/Cut/Dice",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("fry") || lowercasedDirection.contains("sauté")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.PanTool,
            contentDescription = "Fry/Sauté",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("boil") || lowercasedDirection.contains("simmer")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.SoupKitchen,
            contentDescription = "Boil/Simmer",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("rest") || lowercasedDirection.contains("wait") || lowercasedDirection.contains(
            "chill"
        )
    ) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.HourglassEmpty,
            contentDescription = "Rest/Wait/Chill",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("serve") || lowercasedDirection.contains("garnish")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.Restaurant,
            contentDescription = "Serve/Garnish",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("wash") || lowercasedDirection.contains("rinse")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.Wash,
            contentDescription = "Wash/Rinse",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("grind")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.Grain,
            contentDescription = "Grind",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else if (lowercasedDirection.contains("fold")) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.TurnSlightRight,
            contentDescription = "Fold",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else {

        Icon(
            modifier = modifier,
            imageVector = Icons.Rounded.Receipt,
            contentDescription = "Step",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }

}
