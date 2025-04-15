package com.example.late_plate.network

import com.example.late_plate.dummy.Recipe
import kotlinx.serialization.Serializable

@Serializable
data class RecommendatinResponse(
    val recommended_recipes: List<Recipe>
)
