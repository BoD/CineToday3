plugins {
    id("library-conventions")
}

dependencies {
    implementation(projects.domain)
    implementation(projects.api)
    implementation(projects.localStore)

    implementation(libs.kprefs)
}
