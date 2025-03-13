package com.example.late_plate.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    contentPadding:Int=16,
    content:@Composable ()->Unit,
) {
   Card(
       modifier=modifier.shadow(8.dp, shape = RoundedCornerShape(16.dp),clip = false),
       colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) ,
       shape = RoundedCornerShape(16.dp)
   ){
       Box (modifier = Modifier.padding(contentPadding.dp)){
           content()
       }
   }

}