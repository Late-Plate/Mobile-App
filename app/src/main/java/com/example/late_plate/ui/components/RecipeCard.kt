package com.example.late_plate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.dummy.Recipe

@Composable
fun RecipeCard(modifier: Modifier = Modifier, recipe: Recipe, onClick: () -> Unit) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(height = 130.dp, width = 178.dp)
    ) {

        CustomCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp), onClick = onClick

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 38.dp)
            ) {
                Text(
                    recipe.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
                )

//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.Timer,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        recipe.time,
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        fontSize = 12.sp
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Icon(
//                        imageVector = Icons.Outlined.Speed,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        recipe.difficulty,
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        fontSize = 12.sp
//                    )
//
//                }

            }
        }
        OnlineImageCard(
            imageUrl = recipe.imageUrl,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(height = 76.dp, width = 108.dp)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(16.dp)), onClick = onClick
        )
    }

}