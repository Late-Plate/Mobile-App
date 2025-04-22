package com.example.late_plate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoDisturbOn
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SelectedItem(text: String, onClick: (String)->Unit) {
   CustomCard (
           contentPadding = 0
   ) {
       Row(
           modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
               ,
           horizontalArrangement = Arrangement.SpaceBetween,
           verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
       ) {
            Text(text = text,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary,)
           Spacer(Modifier.width(8.dp))
           Icon(
              imageVector = Icons.Outlined.Remove,
               tint = MaterialTheme.colorScheme.primary,
               contentDescription = null,
               modifier = Modifier.clip(shape = CircleShape)
                   .clickable {
                       onClick(text)
                   }
           )
       }
   }
}