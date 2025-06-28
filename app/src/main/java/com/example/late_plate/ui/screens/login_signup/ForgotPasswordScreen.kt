package com.example.late_plate.ui.screens.login_signup

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.late_plate.ui.components.AppLogo
import com.example.late_plate.ui.screens.ForgotPasswordRoute
import com.example.late_plate.ui.screens.LoginRoute
import com.example.late_plate.viewModel.AuthenticationViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    navController: NavController
){
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    val uiState by authenticationViewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isSent) {
        when (uiState.isSent) {
            null -> {
                Log.d("IS SENT", "null")
            }
            else -> {
                Log.d("ISSENT", "not null, ${uiState.isLoading}, ${uiState.message}")

                delay(2000)

                Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")
                Log.d("NavController", "Navigating to Login")
                navController.navigate(LoginRoute) {
                    Log.d("NavController", "Popping up to ForgotPass: inclusive=true")
                    popUpTo(ForgotPasswordRoute) { inclusive = true }
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo()
        Spacer(modifier = Modifier.height(16.dp))
        ForgotPasswordCard(
            sendClick = {email -> authenticationViewModel.resetPassword(email)},
            isLoading = uiState.isLoading,
            isSent = uiState.isSent,
            message = uiState.message
        )
    }
}