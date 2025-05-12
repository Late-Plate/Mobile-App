package com.example.late_plate.network

import kotlinx.serialization.Serializable

@Serializable
data class EdamamResponse(
    val hits: List<EdamamHit>
)
@Serializable
data class EdamamHit(
    val recipe: EdamamRecipe
)
@Serializable
data class EdamamRecipe(
    val images: EdamamImages
)
@Serializable
data class EdamamImages(
    val REGULAR: EdamamImageUrl
)
@Serializable
data class EdamamImageUrl(
    val url: String
)

