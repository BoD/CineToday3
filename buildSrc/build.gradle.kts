plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}


dependencies {
    // Is it possible to use libs.versions.toml?
    implementation("com.android.tools.build:gradle:7.2.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
}
