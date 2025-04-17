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
import com.example.late_plate.navigation.Screen
import com.example.late_plate.ui.components.AppLogo
import com.example.late_plate.viewModel.AuthenticationViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    navController: NavController
){
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    var message by remember { mutableStateOf("") }
    val uiState by authenticationViewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isSent) {
        when (uiState.isSent) {
            null -> {
                Log.d("IS SENT", "null")
            }
            else -> {
                Log.d("ISSENT", "not null, ${uiState.isLoading}, ${uiState.message}")

                // Delay navigation by 3 seconds
                delay(2000)

                // Log before navigating
                Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")

                if (navController.currentDestination?.route != Screen.Login.route) {
                    Log.d("NavController", "Navigating to Login")
                    navController.navigate(Screen.Login.route) {
                        Log.d("NavController", "Popping up to ForgotPass: inclusive=true")
                        popUpTo(Screen.ForgotPass.route) { inclusive = true }
                    }
                } else {
                    Log.d("NavController", "Already at Login screen, no navigation needed")
                }
            }
        }
    }

//    LaunchedEffect(true) {
//        authenticationViewModel.loginEvent.collect { event ->
//            when (event) {
//                is LoginSignupUiEvent.PasswordResetSuccess -> {
//                    message = event.message
//                }
//                is LoginSignupUiEvent.PasswordResetFailed -> {
//                    message = event.message
//                }
//                else -> {}
//            }
//        }
//    }
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
//@Preview
//@Composable
//fun previewForgotPass(){
//    ForgotPasswordScreen()
//}