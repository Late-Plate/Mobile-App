package com.example.late_plate.ui.screens.login_signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.decode.ImageSource
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.CustomCard

import androidx.compose.ui.window.Dialog

@Composable
fun AuthenticationAlert(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier= Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .shadow(8.dp, shape = RoundedCornerShape(16.dp))

        ) {
            Column (
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Spacer(Modifier.height(16.dp))

                    Icon(
                        painter = painterResource(R.drawable.x_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = message,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))


                }
                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    onClick = onDismiss,
                    content = {
                        Text("Ok", Modifier.padding(horizontal = 32.dp), fontSize = 18.sp)
                    }
                )
                Spacer(Modifier.height(16.dp))

            }

        }
    }
}
@Preview
@Composable
fun previewAuthenticationAlert(){
    AuthenticationAlert("Message") { }
}


