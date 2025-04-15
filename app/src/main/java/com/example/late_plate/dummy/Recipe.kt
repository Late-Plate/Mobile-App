package com.example.late_plate.dummy

data class Recipe(
    val title: String,
    val description: String,
    val imageUrl: String,
    val difficulty: String,
    val time: String,
    val ingredients: List<String>,
    val steps: List<String>
)
