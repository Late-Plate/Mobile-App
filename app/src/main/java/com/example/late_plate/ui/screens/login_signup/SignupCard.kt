package com.example.late_plate.ui.screens.login_signup

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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

@Composable
fun SignupCard(
    modifier: Modifier = Modifier,
    signupClick: (String, String, String, String)-> Unit,
    toLoginClick: ()-> Unit
) {
    val scrollState = rememberScrollState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var noInternet by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    val context = LocalContext.current


    val usernameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val confirmPasswordFocus = remember { FocusRequester() }

    if(!isRegistering){
        CustomCard(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier.verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Signup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    "welcome to late plate",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(24.dp))
                CustomTextField(
                    value = username,
                    onValueChange = { newValue -> username = newValue },
                    placeholder = "username",
                    icon = Icons.Outlined.Person,
                    modifier = modifier.focusRequester(usernameFocus),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {emailFocus.requestFocus()}
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = password,
                    onValueChange = { newValue -> password = newValue },
                    placeholder = "password",
                    icon = Icons.Outlined.Password,
                    isPassword = true,
                    modifier = modifier.focusRequester(passwordFocus),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {confirmPasswordFocus.requestFocus()}
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = confirmPassword,
                    onValueChange = { newValue -> confirmPassword = newValue },
                    placeholder = "confirm password",
                    icon = Icons.Outlined.Password,
                    isPassword = true,
                    modifier = modifier.focusRequester(confirmPasswordFocus),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if(!isConnected(context)) {
                                Log.d("SIGNUP", "clicked & no internet")
                                noInternet = true
                            }
                            else
                                signupClick(username, email, password, confirmPassword)
                        }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomButton(
                    onClick = {
                        if(!isConnected(context)) {
                            Log.d("SIGNUP", "clicked & no internet")
                            noInternet = true
                        }
                        else {
                            signupClick(username, email, password, confirmPassword)
                            isRegistering = true
                        }
                    },
                    content = {
                        Text("Signup", Modifier.padding(horizontal = 32.dp), fontSize = 18.sp)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row{
                    Text("already have an account?", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                    Spacer(modifier=Modifier.width(12.dp))
                    Text(
                        text = "login",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp,
                        modifier = Modifier.clickable {toLoginClick() }
                    )
                }
            }
            if(noInternet){
                Log.d("SIGNUP", ".........")
                AuthenticationAlert("Oops! No Internet") {noInternet = false }
            }
        }
    }
    else{
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

}