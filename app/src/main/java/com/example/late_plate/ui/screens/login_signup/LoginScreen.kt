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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.late_plate.navigation.Screen
import com.example.late_plate.ui.components.AppLogo
import com.example.late_plate.viewModel.AuthenticationViewModel
import com.example.late_plate.viewModel.LoginSignupUiEvent


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        authenticationViewModel.loginSignupEvent.collect{ event ->
            Log.d("loginEvent", event.toString())
            when(event){
                is LoginSignupUiEvent.LoginSuccess-> {
                    Log.d("SUCCESS", "")
                    navController.navigate(Screen.Home.route){
                        Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                is LoginSignupUiEvent.LoginFailed->{
                    Toast.makeText(context, "Invalid Email or password", Toast.LENGTH_LONG).show()
                }
                is LoginSignupUiEvent.ToForgotPassScreen->{
                    navController.navigate(Screen.ForgotPass.route){
                        Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")
                        popUpTo(Screen.Login.route) { inclusive = false }
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
        LoginCard(
            loginClick = {email, password ->
                authenticationViewModel.loginUser(email, password)},
            forgetClick = { authenticationViewModel.navigateToForgotPass() }
        )
    }
}