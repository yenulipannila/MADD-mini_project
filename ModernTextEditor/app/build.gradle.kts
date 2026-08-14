plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.moderntexteditor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.moderntexteditor2"
        minSdk = 24
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    // --------------------------------------------------
    // AndroidX
    // --------------------------------------------------

    implementation("androidx.core:core-ktx:1.15.0")

    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("androidx.activity:activity-ktx:1.10.0")

    implementation("androidx.fragment:fragment-ktx:1.8.5")


    // --------------------------------------------------
    // Jetpack Compose
    // --------------------------------------------------

    implementation(platform("androidx.compose:compose-bom:2024.12.01"))

    implementation("androidx.compose.ui:ui")

    implementation("androidx.compose.ui:ui-graphics")

    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")

    implementation("androidx.activity:activity-compose:1.10.0")


    // --------------------------------------------------
    // Compose Tooling
    // --------------------------------------------------

    debugImplementation("androidx.compose.ui:ui-tooling")

    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // --------------------------------------------------
    // Lifecycle
    // --------------------------------------------------

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")


    // --------------------------------------------------
    // Room Database
    // --------------------------------------------------

    implementation("androidx.room:room-runtime:2.6.1")

    implementation("androidx.room:room-ktx:2.6.1")

    ksp("androidx.room:room-compiler:2.6.1")


    // --------------------------------------------------
    // Kotlin Coroutines
    // --------------------------------------------------

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")


    // --------------------------------------------------
    // Material Design
    // --------------------------------------------------

    implementation("com.google.android.material:material:1.12.0")


    // --------------------------------------------------
    // Testing
    // --------------------------------------------------

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}