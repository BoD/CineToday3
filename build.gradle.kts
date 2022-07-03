import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

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

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
//        val reject = setOf("alpha", "beta", "rc")
//        reject.any { candidate.version.contains(it) }
        false
    }
}

// `./gradlew dependencyUpdates` to see new dependency versions
