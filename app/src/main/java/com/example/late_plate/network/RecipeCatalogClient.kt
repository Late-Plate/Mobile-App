package com.example.late_plate.network

import com.example.late_plate.dummy.Recipe
import io.ktor.client.HttpClient
import util.NetworkError
import util.Result

class RecipeCatalogClient(val httpClient: HttpClient) {
    private val baseUrl = "https://gentle-flounder-vigorously.ngrok-free.app/recipes"
    suspend fun getRecipes(page: Int, pageSize: Int): Result<PaginatedResponse, NetworkError> {
        return httpClient.safeGetRequest(
            url = baseUrl,
            params = mapOf("page" to page.toString(), "size" to pageSize.toString())
        )
    }
    suspend fun getRecipesByName(name: String,page: Int,pageSize: Int): Result<PaginatedResponse, NetworkError> {
        return httpClient.safeGetRequest(
            url = "$baseUrl/general-search",
            params = mapOf("term" to name, "page" to page.toString(), "size" to pageSize.toString())
            )
    }
    suspend fun getRecipesByIngredients(ingredient: String,page: Int,pageSize: Int): Result<PaginatedResponse, NetworkError> {
        return httpClient.safeGetRequest(
            url = "$baseUrl/by-ingredient",
            params = mapOf("name" to ingredient, "page" to page.toString(), "size" to pageSize.toString())
            )
    }


}