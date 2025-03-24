package com.example.late_plate.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result
import java.nio.channels.UnresolvedAddressException

class RecipeGenerationClient(
    private val httpClient: HttpClient
) {
    suspend fun generateRecipe(prompt: String): Result<String,NetworkError>{
        val response= try{
            httpClient.post(
                urlString = "https://crow-square-absolutely.ngrok-free.app/llama"
            ){
                contentType(ContentType.Application.Json) // Set content type to JSON
                setBody(GenerateRecipeRequest(prompt))
            }
        }catch (e:UnresolvedAddressException){
            return Result.Error(NetworkError.NO_INTERNET)
        }catch (e:SerializationException){
            return Result.Error(NetworkError.SERIALIZATION)
        }
        return when(response.status.value){
            in 200..299->{
                val recipe=response.body<GeneratedRecipe>()
                Result.Success(recipe.generated_text)
            }
            in 400..499-> {
                Result.Error(NetworkError.REQUEST_TIMEOUT)
            }
            in 500..599-> {
                Result.Error(NetworkError.SERVER_ERROR)
            }
            else -> {
                Result.Error(NetworkError.UNKNOWN)
            }


        }
    }
}