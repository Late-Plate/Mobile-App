import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.0"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
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
        exclude("META-INF/**")  // Exclude unwanted files from dependencies
    }
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
    implementation("com.google.code.gson:gson:2.10.1")

    // Ktor Dependencies (Updated)
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")

    // KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Dependency Injection (Hilt and Koin)
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Koin for Dependency Injection
    implementation("io.insert-koin:koin-android:$koin_version")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

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
}

kapt {
    correctErrorTypes = true
}
