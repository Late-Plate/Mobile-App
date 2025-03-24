package com.example.late_plate.ui.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.dummy.Recipe

@Composable
fun RecommendationCard(modifier: Modifier = Modifier,recipe: Recipe,onClick:(()->Unit)?=null) {
    CustomCard (modifier=Modifier.fillMaxWidth().height(156.dp), onClick = onClick){
        Row (modifier=Modifier.fillMaxWidth()) {
            Column (modifier=Modifier.weight(1f)){
                Text(
                    recipe.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = recipe.steps.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                    , overflow = TextOverflow.Ellipsis
                    , maxLines = 4, lineHeight = 16.sp
             )
//                Row(modifier=Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        imageVector = Icons.Outlined.Timer,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        recipe.time,
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        fontSize = 14.sp
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Icon(
//                        imageVector = Icons.Outlined.Speed,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        recipe.difficulty,
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        fontSize = 14.sp
//                    )
//
//                }
            }
            Spacer(modifier=Modifier.width(16.dp))
            OnlineImageCard(imageUrl = recipe.imageUrl, modifier = Modifier.fillMaxHeight().width(136.dp), onClick = onClick)
        }
    }

}