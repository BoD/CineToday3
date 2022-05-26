plugins {
    id("library-conventions")
    alias(libs.plugins.sqldelight)
}

dependencies {
    implementation(libs.sqldelight.androidDriver)
    implementation(libs.sqldelight.coroutines)
}
