package com.example.late_plate.network

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse(val content:List<RecipeModel>)
