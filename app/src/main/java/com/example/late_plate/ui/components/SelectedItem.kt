package com.example.late_plate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoDisturbOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SelectedItem(text: String) {
   Card(
       modifier = Modifier
           .fillMaxWidth()
           .padding(4.dp),
       colors = CardDefaults.cardColors(
           containerColor = MaterialTheme.colorScheme.surface,
       )
   ) {
       Row(
           modifier = Modifier
               .fillMaxWidth()
               .height(45.dp),
           horizontalArrangement = Arrangement.SpaceBetween,
           verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
       ) {
            Text(text = text,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 10.dp))
           Icon(
              imageVector = Icons.Outlined.DoDisturbOn,
               tint = MaterialTheme.colorScheme.primary,
               contentDescription = null,
               modifier = Modifier.padding(end=10.dp)
           )
       }


   }
}