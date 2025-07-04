package com.example.late_plate.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result
import java.nio.channels.UnresolvedAddressException

suspend inline fun <reified T> HttpClient.safeGetRequest(
    url: String, params: Map<String, String> = emptyMap()
): Result<T, NetworkError> {
    return try {
        val response = get(url) {
            params.forEach { (key, value) ->
                parameter(key, value)
            }
        }
        when (response.status.value) {
            in 200..299 -> {
                Result.Success(response.body<T>())
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
    } catch (e: UnresolvedAddressException) {
        Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        Result.Error(NetworkError.SERIALIZATION)
    }
}