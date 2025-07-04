package com.example.late_plate.ui.screens.login_signup
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.late_plate.R
import com.example.late_plate.ui.components.AppLogo
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.screens.ForgotPasswordRoute
import com.example.late_plate.ui.screens.HomeRoute
import com.example.late_plate.ui.screens.LoginRoute
import com.example.late_plate.ui.screens.SignupRoute
import com.example.late_plate.viewModel.AuthenticationViewModel
import com.example.late_plate.viewModel.LoginSignupUiEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException



@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel
) {

    val loginAlertState by authenticationViewModel.loginAlert.collectAsState()
    val isLoading by authenticationViewModel.isLoadingLogin.collectAsState()
    var loginSuccessHandled by remember { mutableStateOf(false) }
    var noInternet by remember { mutableStateOf(false) }

    if(loginAlertState){
        AuthenticationAlert(
            "Incorrect Email/Password",
            {authenticationViewModel.loginAlert.value = false }
        )
    }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        authenticationViewModel.loginSignupEvent.collect{ event ->
            Log.d("loginEvent", event.toString())
            when(event){
                is LoginSignupUiEvent.LoginSuccess-> {
                    if (!loginSuccessHandled) {
                        loginSuccessHandled = true
                        navController.navigate(HomeRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                }
                is LoginSignupUiEvent.ToForgotPassScreen->{
                    navController.navigate(ForgotPasswordRoute){
                        Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")
                        popUpTo(LoginRoute) { inclusive = false }
                    }
                }
                is LoginSignupUiEvent.ToSignupScreen->{
                    navController.navigate(SignupRoute){
                        Log.d("NavController", "Current destination: ${navController.currentDestination?.route}")
                        popUpTo(LoginRoute) { inclusive = false }
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
        if (loginSuccessHandled) {
            return
        }

        AppLogo()
        Spacer(modifier = Modifier.height(16.dp))
        if(isLoading){
            Column(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )

            }
        }
        else{
            LoginCard(
                loginClick = {email, password ->
                    authenticationViewModel.loginUser(email, password)},
                forgetClick = { authenticationViewModel.navigateToForgotPass() },
                signupClick = {authenticationViewModel.navigateToSignUp()},
                authenticationViewModel = authenticationViewModel
            )
        }

    }
}