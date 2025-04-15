package com.example.late_plate.dummy

import StringAsListSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val title: String,
    val description: String="",
    val imageUrl: String="",
    val difficulty: String="",
    val time: String="",
    @Serializable(with = StringAsListSerializer::class)
    val ingredients: List<String>,
    @Serializable(with = StringAsListSerializer::class)
    val directions: List<String>
)
