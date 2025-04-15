package com.example.late_plate.network

import kotlinx.serialization.Serializable

@Serializable
data class RecommendatinResponse(
    val recommended_recipes: List<RecipeResponse>
)
