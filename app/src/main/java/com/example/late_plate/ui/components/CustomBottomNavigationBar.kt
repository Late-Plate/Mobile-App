package com.example.late_plate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.FABState

@Composable
fun CustomBottomNavigationBar(fabState: FABState) {
    var selected by remember { mutableStateOf("Home") }

    Box(
        modifier = Modifier
            .height(108.dp)
            .fillMaxWidth()
            .graphicsLayer {
                clip = false
            }
    ) {

        Card(
            modifier = Modifier
                .height(86.dp)
                .align(alignment = Alignment.BottomCenter)
                .shadow(16.dp, RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp))
                .offset(y = 8.dp),

            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),

            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItem(
                    icons = Icons.Filled.Fastfood to Icons.Outlined.Fastfood,
                    label = "Home",
                    isSelected = selected == "Home",
                    onClick = { selected = "Home" }
                )
                BottomNavItem(
                    icons = Icons.Filled.Inventory2 to Icons.Outlined.Inventory2,
                    label = "Inventory",
                    isSelected = selected == "Inventory",
                    onClick = { selected = "Inventory" }
                )

                Spacer(modifier = Modifier.width(56.dp))
                BottomNavItem(
                    icons = Icons.Filled.Bookmark to Icons.Outlined.BookmarkBorder,
                    label = "Saved",
                    isSelected = selected == "Saved",
                    onClick = { selected = "Saved" }
                )
                BottomNavItem(
                    icons = Icons.Filled.Person to Icons.Outlined.Person,
                    label = "Profile",
                    isSelected = selected == "Profile",
                    onClick = { selected = "Profile" })
            }

        }
        CustomFloatingActionButton(
            modifier = Modifier.align(alignment = Alignment.TopCenter), fabState = fabState
        )
    }


}

@Composable
fun BottomNavItem(
    icons: Pair<ImageVector, ImageVector>,
    label: String,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(height = 48.dp, width = 68.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                if (isSelected) lerp(
                    MaterialTheme.colorScheme.background,
                    Color.Black, 0.04f
                ) else Color.Transparent
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }) {
        Icon(
            imageVector = if (isSelected) icons.first else icons.second,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CustomFloatingActionButton(
    modifier: Modifier, fabState: FABState
) {
    Box(
        modifier = modifier
            .shadow(2.dp, shape = RoundedCornerShape(16.dp))
            .size(52.dp),
        contentAlignment = Alignment.Center
    )
    {
        FloatingActionButton(
            onClick = fabState.onClick.value,
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxSize(),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)


        ) {
            Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                imageVector = fabState.icon.value,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}



