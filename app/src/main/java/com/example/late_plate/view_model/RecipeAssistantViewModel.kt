package com.example.late_plate.view_model

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class RecipeAssistantViewModel: ViewModel(){
    private var _stepIndex = mutableStateOf(0)
    var stepIndex: State<Int> = _stepIndex

    private val _stepsTimers = mutableMapOf<Int, TimerState>()

    fun goToNextStep(totalSteps: Int){
        if (_stepIndex.value < totalSteps - 1)
            _stepIndex.value++
    }

    fun goToPreviousStep(){
        if(_stepIndex.value > 0)
            _stepIndex.value--
    }


    fun getOrCreateTimer(stepIndex: Int, totalTime: Long): TimerState {
        return _stepsTimers.getOrPut(stepIndex) {
            TimerState(
                totalTime = totalTime,
                timeLeft = mutableStateOf(totalTime),
                isRunning = mutableStateOf(false),
                currentTimer = null
            )
        }
    }

    fun startTimer(stepIndex: Int) {
        val timerState = _stepsTimers[stepIndex] ?: return
        if (timerState.isRunning.value) return

        timerState.currentTimer?.cancel()

        timerState.currentTimer = object : CountDownTimer(timerState.timeLeft.value, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerState.timeLeft.value = millisUntilFinished
            }

            override fun onFinish() {
                timerState.isRunning.value = false
                timerState.timeLeft.value = 0
            }
        }.start()
        timerState.isRunning.value = true
    }

    fun resetTimer(stepIndex: Int) {
        val timerState = _stepsTimers[stepIndex] ?: return
        timerState.currentTimer?.cancel()
        timerState.timeLeft.value = timerState.totalTime
        timerState.isRunning.value = false
    }

    override fun onCleared() {
        _stepsTimers.values.forEach { it.currentTimer?.cancel() }
        super.onCleared()
    }
}

data class TimerState(
    val totalTime: Long,
    val timeLeft: MutableState<Long>,
    val isRunning: MutableState<Boolean>,
    var currentTimer: CountDownTimer? = null
)
