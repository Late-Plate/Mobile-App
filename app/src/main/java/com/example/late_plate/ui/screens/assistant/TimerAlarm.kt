package com.example.late_plate.ui.screens.assistant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.late_plate.R
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.components.CustomCard

@Composable
fun TimerAlarmDialog(
    recipeName: String,
    stepIndex: Int,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier= Modifier.shadow(8.dp, shape = RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) ,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
                , modifier = Modifier.wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Text(
                        text = recipeName,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.alarm_icon),
                        contentDescription = "Alarm Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Timer of Step ${stepIndex + 1} is Complete!",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Text(
                    "okay",
                    modifier = Modifier.padding(bottom = 16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    , color = MaterialTheme.colorScheme.primary
                )


            }
        }
    }
}
