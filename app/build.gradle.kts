plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "org.jraf.android.cinetoday"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
//        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("debug").java.srcDirs("src/debug/kotlin")
    }
}

dependencies {
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.kotlinx.coroutines.android)

    implementation("com.google.android.gms:play-services-wearable:17.1.0")
    implementation("androidx.wear:wear:1.2.0")

    implementation(projects.repository)
}

kapt {
    // See https://developer.android.com/training/dependency-injection/hilt-android
    correctErrorTypes = true
}

hilt {
    // See https://dagger.dev/hilt/gradle-setup#aggregating-task
    enableAggregatingTask = true
}
