package com.example.late_plate.network

import com.example.late_plate.dummy.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

class RecipeGenerationClient (
    private val httpClient: HttpClient
) {
    suspend fun getTopRecipes(userId:Int): Result<List<Recipe>, NetworkError> {
        val response = try {
            httpClient.post(
                urlString = "https://quagga-creative-pika.ngrok-free.app/combined_recommendations"
            ) {
                contentType(ContentType.Application.Json) // Set content type to JSON
                setBody(RecommendatinRequest(userId))
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }
        return when (response.status.value) {
            in 200..299 -> {
                val wrapper = response.body<RecommendatinResponse>()
                Result.Success(wrapper.recommended_recipes)
            }

            in 400..499 -> {
                Result.Error(NetworkError.REQUEST_TIMEOUT)
            }

            in 500..599 -> {
                Result.Error(NetworkError.SERVER_ERROR)
            }

            else -> {
                Result.Error(NetworkError.UNKNOWN)
            }


        }
    }
    suspend fun generateRecipes(prompt: String): Pair<Result<String, NetworkError>, Result<String, NetworkError>> {
        return coroutineScope {
            val deferredRecipe1 = async { generateRecipe(prompt, "gpt") }
            val deferredRecipe2 = async { generateRecipe(prompt, "llama") }

            deferredRecipe1.await() to deferredRecipe2.await()
        }
    }
    suspend fun generateRecipe(prompt: String,model:String): Result<String,NetworkError>{
        val response= try{
            httpClient.post(
                urlString = "https://crow-square-absolutely.ngrok-free.app/$model"
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