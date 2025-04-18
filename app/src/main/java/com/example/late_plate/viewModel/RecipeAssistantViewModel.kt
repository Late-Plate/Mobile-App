package com.example.late_plate.viewModel

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.dummy.Recipe
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RecipeAssistantViewModel: ViewModel(){

    private val _alarmEvents = MutableSharedFlow<RecipeTimerKey>()
    val alarmEvents = _alarmEvents.asSharedFlow()

    // Track ALL timer states across recipes
    private val _allTimerStates = mutableStateMapOf<RecipeTimerKey, TimerState>()
    val allTimerStates: SnapshotStateMap<RecipeTimerKey, TimerState> = _allTimerStates

    // Track ACTIVE CountDownTimer instances for ALL recipes
    private val activeTimers = mutableMapOf<RecipeTimerKey, CountDownTimer>()

    // Track finished timers needing alerts
    private val _finishedTimers = mutableStateMapOf<RecipeTimerKey, AlarmState>()
    val finishedTimers: SnapshotStateMap<RecipeTimerKey, AlarmState> = _finishedTimers

    // Current recipe state
    private var _currentRecipe = mutableStateOf<Recipe?>(null)
    val currentRecipe: State<Recipe?> = _currentRecipe

    private var _stepIndex = mutableStateOf(0)
    var stepIndex: State<Int> = _stepIndex

    val recipeTimerKey: State<RecipeTimerKey?> = derivedStateOf {
        if (currentRecipe.value != null) {
            RecipeTimerKey(recipeName = currentRecipe.value!!.title, stepIndex = stepIndex.value)
        } else {
            null
        }
    }


    fun loadRecipe(recipe: Recipe) {
        _currentRecipe.value = recipe
        _stepIndex.value = 0

        recipe.directions.forEachIndexed { index, step ->
            extractTime(step)?.let { duration ->
                val key = RecipeTimerKey(recipe.title, index)
                _allTimerStates.getOrPut(key) {
                    TimerState(
                        key = key,
                        totalTime = duration,
                        timeLeft = duration,
                        isRunning = false
                    )
                }
            }
        }
    }

    fun startTimer(recipeId: String, stepIndex: Int) {
        val key = RecipeTimerKey(recipeId, stepIndex)
        val timerState = _allTimerStates[key] ?: return

        activeTimers[key]?.cancel()

        val newTimer = object : CountDownTimer(timerState.timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _allTimerStates[key] = timerState.copy(
                    timeLeft = millisUntilFinished,
                    isRunning = true
                )
            }

            override fun onFinish() {
                _allTimerStates[key] = timerState.copy(
                    timeLeft = 0,
                    isRunning = false
                )
                _finishedTimers[key] = AlarmState(key)
                activeTimers.remove(key)
                // Emit alarm event
                viewModelScope.launch {
                    _alarmEvents.emit(key)
                }
            }
        }.start()

        activeTimers[key] = newTimer
    }

    fun dismissAlarm(key: RecipeTimerKey) {
        _finishedTimers.remove(key)
    }

    fun resetTimer(recipeId: String, stepIndex: Int) {
        val key = RecipeTimerKey(recipeId, stepIndex)
        activeTimers[key]?.cancel()
        _allTimerStates[key]?.let { state ->
            _allTimerStates[key] = state.copy(
                timeLeft = state.totalTime,
                isRunning = false
            )
        }
        activeTimers.remove(key)
    }


    private fun updateTimerState(key: RecipeTimerKey, timeLeft: Long, isRunning: Boolean) {
        _allTimerStates[key]?.let { existingState ->
            _allTimerStates[key] = existingState.copy(
                timeLeft = timeLeft,
                isRunning = isRunning
            )
        }
    }

    override fun onCleared() {
        activeTimers.values.forEach { it.cancel() }
        super.onCleared()
    }

    fun goToNextStep(totalSteps: Int){
        if (_stepIndex.value < totalSteps - 1)
            _stepIndex.value++
    }

    fun goToPreviousStep(){
        if(_stepIndex.value > 0)
            _stepIndex.value--
    }

}

data class TimerState(
    val key: RecipeTimerKey,
    val totalTime: Long,
    val timeLeft: Long,
    val isRunning: Boolean
)

data class AlarmState(
    val key: RecipeTimerKey,
    val timestamp: Long = System.currentTimeMillis()
)

data class RecipeTimerKey(
    val recipeName: String,
    val stepIndex: Int
)

fun extractTime(step: String): Long {
    val words = step.split(" ", "-")
    var totalMilliseconds: Long = 0

    words.forEachIndexed { index, word ->
        when {
            word.lowercase().startsWith("hour") -> {
                val numberPart = words.getOrNull(index - 1)
                val hours = numberPart?.replace(Regex("\\D"), "")?.toLongOrNull() ?: 1L
                totalMilliseconds += hours * 60 * 60 * 1000
            }
            word.lowercase().startsWith("minute") -> {
                val numberPart = words.getOrNull(index - 1)
                val minutes = numberPart?.replace(Regex("\\D"), "")?.toLongOrNull() ?: 1L
                totalMilliseconds += minutes * 60 * 1000
            }
            word.lowercase().startsWith("second") -> {
                val numberPart = words.getOrNull(index - 1)
                val seconds = numberPart?.replace(Regex("\\D"), "")?.toLongOrNull() ?: 1L
                totalMilliseconds += seconds * 1000
            }
        }
    }
    Log.d("TIMER Extracted", "Time: $totalMilliseconds ms")
    return totalMilliseconds
}