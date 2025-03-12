package com.example.late_plate.ui.screens.login

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.CustomTextField

@Composable
fun LoginCard(modifier: Modifier = Modifier) {
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
                icon = Icons.Outlined.Email
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = password,
                onValueChange = { newValue -> password = newValue },
                placeholder = "password",
                icon = Icons.Outlined.Password,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "forget password?",
                modifier = Modifier.align(Alignment.End),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                onClick = {},
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
                    color = MaterialTheme.colorScheme.onPrimary
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier=Modifier.width(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                IconButton(onClick = {},modifier=Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
                Spacer(modifier=Modifier.width(16.dp))
                IconButton(onClick = {},modifier=Modifier.size(36.dp) ){
                    Icon(

                        painter = painterResource(id = R.drawable.facebook),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row{
                Text("don't have and account?", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
                Spacer(modifier=Modifier.width(12.dp))
                Text("signup", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            }
        }
    }
}