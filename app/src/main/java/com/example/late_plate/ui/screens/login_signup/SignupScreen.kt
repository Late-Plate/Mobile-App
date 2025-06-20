package com.example.late_plate.ui.screens.login_signup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.late_plate.navigation.Screen
import com.example.late_plate.ui.components.AppLogo
import com.example.late_plate.ui.screens.LoginRoute
import com.example.late_plate.ui.screens.SignupRoute
import com.example.late_plate.viewModel.AuthenticationViewModel
import com.example.late_plate.viewModel.LoginSignupUiEvent

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()

    val signupAlertState by authenticationViewModel.signupAlert.collectAsState()

    if(signupAlertState){
        AuthenticationAlert(
            "Email already in use",
            { authenticationViewModel.signupAlert.value = false }
        )
    }

    LaunchedEffect(Unit){
        authenticationViewModel.loginSignupEvent.collect{ event->
            when(event){
                is LoginSignupUiEvent.FromSignupToLogin->{
                    navController.navigate(LoginRoute){
                        Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")
                        popUpTo(SignupRoute) { inclusive = true }
                    }
                }
                is LoginSignupUiEvent.SignupSuccess->{
                    navController.navigate(LoginRoute){
                        Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")
                        popUpTo(SignupRoute) { inclusive = true }
                    }
                }
                else->{}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AppLogo()
        Spacer(modifier = Modifier.height(16.dp))
        SignupCard(
            signupClick = {username, email, pass, confirm -> authenticationViewModel.registerUser(
                username, email, pass, confirm
            )},
            toLoginClick = {authenticationViewModel.navigateFromSignupToLogin()}
        )

    }
}
