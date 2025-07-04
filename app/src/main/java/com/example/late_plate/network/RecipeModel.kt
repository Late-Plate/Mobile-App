package com.example.late_plate.network

import kotlinx.serialization.Serializable

@Serializable
data class RecipeModel(val id:Int, val name:String, val ingredients:String, val instructions:String)
