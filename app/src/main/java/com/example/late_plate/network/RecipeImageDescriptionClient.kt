package com.example.late_plate.network

import android.util.Log
import com.example.late_plate.dummy.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import java.net.URLEncoder

class RecipeImageDescriptionClient(
    private val httpClient: HttpClient
) {
    private val accessKey = "2Ck1bQTKtU8jdPo13uCRjkMCKjthGerI5ZCp77jHEqU"
    private val geminiApiKey = "AIzaSyCfP5Ob-fsYZ-eXzBDRJg_yiVzezLMHUlw"

    suspend fun fetchImageUrlFromEdamam(recipeName: String): String? {
//        val query = URLEncoder.encode("$recipe.title", "UTF-8")
        val appId = "02d6766a"
        val appKey = "67f4487dd83084f501edfb2f43855447"
        val url = "https://api.edamam.com/api/recipes/v2?type=public&q=${recipeName} food&app_id=$appId&app_key=$appKey"

        return try {
            val response: EdamamResponse = httpClient.get(url) {
                headers {
                    append("Edamam-Account-User", "your_user_id_here") // Add this line
                }
            }.body()

            response.hits.firstOrNull()?.recipe?.images?.REGULAR?.url
        } catch (e: Exception) {
            Log.e("EdamamAPI", "Error: ${e.message}")
            null
        }
    }

    suspend fun generateRecipeDescriptionGemini(recipeName: String, directions: String): String? {
        val prompt = "Write a short description for this recipe:\n\nName: $recipeName\nDirections: $directions"

        val requestBody = buildJsonObject {
            putJsonArray("contents") {
                addJsonObject {
                    putJsonArray("parts") {
                        addJsonObject {
                            put("text", prompt)
                        }
                    }
                }
            }
        }

        return try {
            val response: JsonObject = httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$geminiApiKey") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val candidates = response["candidates"]?.jsonArray
            val firstCandidate = candidates?.getOrNull(0)?.jsonObject
            val content = firstCandidate?.get("content")?.jsonObject
            val parts = content?.get("parts")?.jsonArray
            val firstPart = parts?.getOrNull(0)?.jsonObject
            val text = firstPart?.get("text")?.jsonPrimitive?.content
            Log.d("TEXT", text.toString())

            text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
