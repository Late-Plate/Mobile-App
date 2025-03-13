package com.example.late_plate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomBottomNavigationBar(modifier: Modifier = Modifier) {
    var selected by remember { mutableStateOf("Home") }

    val dockRadius = with(LocalDensity.current) { 38.dp.toPx() }
    Box(contentAlignment = Alignment.Center) {
        BottomAppBar(
            modifier = modifier
                .clip(BottomNavShape(dockRadius))
                .height(86.dp),
            containerColor = lerp(MaterialTheme.colorScheme.background, Color.Black, 0.03f),

            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                Spacer(modifier = Modifier.width(68.dp))

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
            icon = Icons.Outlined.PlayArrow,
            behaviour = {},
            modifier = Modifier.offset(y = (-86).dp)
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, // Explicitly provide interactionSource
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
    modifier: Modifier = Modifier,
    icon: ImageVector,
    behaviour: () -> Unit
) {
    FloatingActionButton(
        onClick = behaviour,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .offset(y = (48).dp)
            .shadow(8.dp, shape = CircleShape, clip = false)

    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

class BottomNavShape(
    private val dockRadius: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val baseRect = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset.Zero, Offset(size.width, size.height)),

                    ),
            )
        }

        val rect1 = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset.Zero, Offset(size.width / 2 - dockRadius + 4f, size.height)),

                    ),
            )
        }

        val rect1A = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(Offset.Zero, Offset(size.width / 2 - dockRadius + 4f, size.height)),

                    topRight = CornerRadius(32f, 32f),
                ),
            )
        }

        val rect1B = Path.combine(PathOperation.Difference, rect1, rect1A)

        val rect2 = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(
                        Offset(size.width / 2 + dockRadius - 4f, 0f),
                        Offset(size.width, size.height)
                    ),

                    ),
            )
        }

        val rect2A = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(
                        Offset(size.width / 2 + dockRadius - 4f, 0f),
                        Offset(size.width, size.height)
                    ),

                    topLeft = CornerRadius(32f, 32f),
                ),
            )
        }

        val rect2B = Path.combine(PathOperation.Difference, rect2, rect2A)

        val circle = Path().apply {
            addOval(
                Rect(
                    Offset(size.width / 2 - dockRadius, -dockRadius),
                    Offset(size.width / 2 + dockRadius, dockRadius),
                ),
            )
        }

        val path1 = Path.combine(PathOperation.Difference, baseRect, circle)
        val path2 = Path.combine(PathOperation.Difference, path1, rect1B)
        val path = Path.combine(PathOperation.Difference, path2, rect2B)

        return Outline.Generic(path)
    }
}

