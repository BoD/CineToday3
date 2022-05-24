plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}


dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
}
