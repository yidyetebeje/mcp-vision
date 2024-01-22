import com.android.build.api.dsl.AaptOptions

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // include kpat
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.yidye.mcp_vision"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yidye.mcp_vision"
        minSdk = 26
        targetSdk = 34
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.paging.compose)
    implementation("androidx.compose.ui:ui-util:1.5.4")

    val paging_version = "3.2.1"

    implementation("androidx.paging:paging-runtime:$paging_version")



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.camera:camera-camera2:1.3.0-alpha06")
    implementation("androidx.camera:camera-lifecycle:1.3.0-alpha06")
    implementation("androidx.camera:camera-view:1.3.0-alpha06")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44.1")
    kapt("com.google.dagger:hilt-android-compiler:2.44.1")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    // Coil
    implementation("io.coil-kt:coil-compose:2.2.0")
    implementation("io.coil-kt:coil:2.2.0")

    // Accompanist
    implementation("com.google.accompanist:accompanist-navigation-animation:0.23.0")
    implementation("com.google.accompanist:accompanist-permissions:0.19.0")

    // MLKit
    implementation("com.google.mlkit:face-detection:16.1.5")
    implementation("com.google.mlkit:image-labeling:17.0.7")
    implementation("com.google.mlkit:object-detection:17.0.0")
    implementation("com.google.mlkit:barcode-scanning:17.1.0")
    implementation("com.google.mlkit:text-recognition:16.0.0-beta6")
    implementation("com.google.mlkit:face-mesh-detection:16.0.0-beta1")
    implementation("com.google.mlkit:object-detection-custom:17.0.1")
    val tfLiteVersion = "0.4.0"
    implementation("org.tensorflow:tensorflow-lite-task-vision:$tfLiteVersion")
    implementation("org.tensorflow:tensorflow-lite-gpu-delegate-plugin:$tfLiteVersion")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.9.0")

}
kapt {
    correctErrorTypes = true
}