plugins {
    id("library-conventions")
}

dependencies {
    implementation(projects.api)
    implementation(projects.localStore)
    implementation(libs.kprefs)
}
