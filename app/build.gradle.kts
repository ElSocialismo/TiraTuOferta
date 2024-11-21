plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.tiratuoferta"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tiratuoferta"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging:24.0.3")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.1.0")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Kotlin Parcelize Runtime
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:1.8.0")

    // AndroidX Libraries
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.8.0")

    // Compose Material (Material 2 actualizado)
    implementation("androidx.compose.material:material:1.6.0")

    // Glide (para cargar imágenes)
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.core.ktx)
    implementation(libs.androidx.espresso.intents)
    implementation(libs.androidx.ui.test.junit4.android)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Navigation en Compose
    implementation(libs.androidx.navigation.compose)

    // Testing Libraries
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.mockito:mockito-core:5.6.0")

    // Pruebas Instrumentadas
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")

    // Debugging Libraries
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.1")

    // Kotlin Script Runtime
    implementation(kotlin("script-runtime"))
    implementation(kotlin("test"))
}
tasks.withType<Test> {
    useJUnitPlatform() // Necesario para JUnit 5
}
