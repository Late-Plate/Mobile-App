package com.example.late_plate.ui.screens.login_signup

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.CustomTextField
import com.example.late_plate.util.isConnected
import com.example.late_plate.viewModel.AuthenticationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginCard(
    modifier: Modifier = Modifier,
    loginClick:(String, String)-> Unit,
    forgetClick: ()-> Unit,
    signupClick: ()-> Unit,
    authenticationViewModel: AuthenticationViewModel
    ) {

    val context = LocalContext.current
    var noInternetGoogle by remember {mutableStateOf(false)}
    var noInternet by remember { mutableStateOf(false) }

    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // from google-services.json
            .requestEmail()
            .build()
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                authenticationViewModel.firebaseAuthWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Log.e("GOOGLE_SIGN_IN", "Google sign in failed", e)
        }
    }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    CustomCard(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Login",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                "welcome back",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextField(
                value = email,
                onValueChange = { newValue -> email = newValue },
                placeholder = "email",
                icon = Icons.Outlined.Email,
                modifier = modifier.focusRequester(emailFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        passwordFocus.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = password,
                onValueChange = { newValue -> password = newValue },
                placeholder = "password",
                icon = Icons.Outlined.Password,
                isPassword = true,
                modifier = modifier.focusRequester(passwordFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if(isConnected(context))
                            loginClick(email, password)
                        else
                            noInternet = true
                    }
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "forget password?",
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { forgetClick() },
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                onClick = {
                    if(isConnected(context))
                        loginClick(email, password)
                    else
                        noInternet = true
                    },
                content = {
                    Text("login", Modifier.padding(horizontal = 32.dp), fontSize = 18.sp)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier=Modifier.width(16.dp))
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "or",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier=Modifier.width(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        if (isConnected(context)) {
                            launcher.launch(googleSignInClient.signInIntent)
                        } else {
                            Log.w("GOOGLE_SIGN_IN", "No internet connection")
                            noInternetGoogle = true
                        }
                    },
                    modifier=Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row{
                Text("don't have and account?", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                Spacer(modifier=Modifier.width(12.dp))
                Text(
                    text = "signup",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { signupClick() }
                )
            }
        }
    }
    if(noInternetGoogle || noInternet){
        AuthenticationAlert("Oops! No Internet") {
            noInternetGoogle = false
            noInternet = false
        }
    }
}