package com.example.late_plate.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ), onClick = onClick
        , shape = RoundedCornerShape(12.dp)
    ) { content() }
}