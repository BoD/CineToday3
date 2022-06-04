plugins {
    alias(libs.plugins.benManes.versions)
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.hilt.gradlePlugin)
    }
}

tasks {
    register<Delete>("clean") {
        delete(rootProject.buildDir)
    }
}

// `./gradlew dependencyUpdates` to see new dependency versions
