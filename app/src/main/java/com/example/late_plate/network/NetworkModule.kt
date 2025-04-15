package com.example.late_plate.network
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import com.example.late_plate.network.RecipeGenerationClient
import com.example.late_plate.network.createHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideHttpClient(): HttpClient {
        return createHttpClient(Android.create())
    }

    @Provides
    fun provideRecipeClient(httpClient: HttpClient): RecipeGenerationClient {
        return RecipeGenerationClient(httpClient)
    }
}
