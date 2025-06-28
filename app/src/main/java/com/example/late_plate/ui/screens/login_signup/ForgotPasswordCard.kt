package com.example.late_plate.ui.screens.login_signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.CustomTextField
import com.example.late_plate.util.isConnected

@Composable
fun ForgotPasswordCard(
    modifier: Modifier = Modifier,
    sendClick: (String)-> Unit,
    isLoading: Boolean,
    isSent: Boolean?,
    message: String
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var noInternet by remember { mutableStateOf(false) }

    CustomCard(modifier = Modifier.fillMaxSize()) {
        if(!isLoading){
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.primary_transparent))
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.primary_semitransparent))
                            .align(Alignment.Center)
                    )
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Forgot Password?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    "we will send you an email shortly",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))
                CustomTextField(
                    value = email,
                    onValueChange = { newValue -> email = newValue },
                    placeholder = "email",
                    icon = Icons.Outlined.Email
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomButton(
                    onClick = {
                        if(isConnected(context))
                            sendClick(email)
                        else
                            noInternet = true
                    },
                    content = {
                        Text("Send", Modifier.padding(horizontal = 32.dp), fontSize = 18.sp)
                    }
                )
            }
            if(noInternet){
                AuthenticationAlert("Oops! No Internet") {noInternet = false }
            }
        }
        else{
            Column(
                modifier = modifier.fillMaxSize()
                    .padding(top = if(isSent == null) 0.dp else 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (isSent == null) Arrangement.Center else Arrangement.Top
            ){
                if(isSent == null) CircularProgressIndicator()
                else {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.primary_transparent))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(colorResource(R.color.primary_semitransparent))
                                .align(Alignment.Center)
                        )
                        Icon(
                            imageVector = if(isSent == true) Icons.Filled.Done else Icons.Filled.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.align(Alignment.Center).size(90.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = message,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun previewForgotCard(){
//    ForgotPasswordCard(isLoading = false, isSent = false, message = "")
//}