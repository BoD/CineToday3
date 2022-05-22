buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
    }
}

plugins {
    kotlin("android").version(libs.versions.kotlin).apply(false)
    id("com.android.library").version(libs.versions.agp).apply(false)
    id("com.android.application").version(libs.versions.agp).apply(false)
}

tasks {
    register<Delete>("clean") {
        delete(rootProject.buildDir)
    }
}
