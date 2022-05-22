import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.apollo)
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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

    // Should be implementation but needs to be api for hilt to be able to generate code in dependend modules
    api(libs.apollo)

    testImplementation(libs.testing.junit)
}

apollo {
    packageName.set("org.jraf.android.cinetoday.api")
    generateAsInternal.set(true)
    codegenModels.set("responseBased")
}

kapt {
    // See https://developer.android.com/training/dependency-injection/hilt-android
    correctErrorTypes = true
}
