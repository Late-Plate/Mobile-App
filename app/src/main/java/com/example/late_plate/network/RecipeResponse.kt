package com.example.late_plate.network

import com.example.late_plate.viewModel.IngredientsViewModel
import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    @Serializable(with = StringAsListSerializer::class)
    val directions: List<String>,

    @Serializable(with = StringAsListSerializer::class)
    val ingredients: List<String>,

    val title: String
)
