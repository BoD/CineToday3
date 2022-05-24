plugins {
    id("library-conventions")
    alias(libs.plugins.apollo)
}

dependencies {
    implementation(libs.apollo)
}

apollo {
    packageName.set("org.jraf.android.cinetoday.api")
    generateAsInternal.set(true)
    codegenModels.set("responseBased")
}
