package com.example.late_plate.ui.components

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.late_plate.model.Classification

@Composable
fun DetectedIngredientCard(modifier: Modifier = Modifier,classification: Classification) {
    CustomCard(modifier=modifier.wrapContentWidth(),contentPadding=8) {Text("${classification.name}, score %${(classification.score*100).toInt()}")}
}