import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.0"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.late_plate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.late_plate"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    // To avoid deprecated warnings, you can add packaging options here
    packagingOptions {
        jniLibs {
            excludes += setOf("META-INF/**")
        }
        resources {
            excludes += setOf("META-INF/**")
        }
    }

    // To avoid deprecated warnings, you can add packaging options here
}

val ktor_version: String by project
val coroutines_version: String by project
val koin_version: String by project

configurations.all {
    exclude(group = "com.google.ai.edge.litert")  // Exclude problematic dependency
}

dependencies {
    // Core and Lifecycle dependencies
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.v154)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.credentials)

    // CameraX Dependencies (ensure latest version)
    implementation(libs.androidx.camera.core.v141)
    implementation(libs.androidx.camera.camera2.v141)
    implementation(libs.androidx.camera.lifecycle.v141)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view.v141)
    implementation(libs.androidx.camera.extensions)

    // TensorFlow Lite (ensure latest version)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.gpu.delegate.plugin)
    implementation(libs.tensorflow.lite.gpu)
    // Navigation and Compose
    implementation(libs.androidx.navigation.compose)

    // JSON Handling
    implementation(libs.gson.v2101)

    // Ktor Dependencies (Updated)
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")

    // KotlinX Serialization
    implementation(libs.kotlinx.serialization.json.v170)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation(libs.kotlinx.coroutines.android)

    // Dependency Injection (Hilt and Koin)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation(libs.firebase.firestore.ktx)
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Koin for Dependency Injection
    implementation(libs.koin.android)

    // Navigation
    implementation(libs.androidx.navigation.compose.v277)

    // UI and Icons
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.androidx.navigation.compose)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation("androidx.credentials:credentials:1.3.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0") // or latest version

    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    //
    implementation("androidx.compose.foundation:foundation")


}

kapt {
    correctErrorTypes = true
}
