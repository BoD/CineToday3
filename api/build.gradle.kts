plugins {
    id("library-conventions")
    alias(libs.plugins.apollo)
}

dependencies {
    // Should be implementation but needs to be api for hilt to be able to generate code in dependend modules
    api(libs.apollo)
}

apollo {
    packageName.set("org.jraf.android.cinetoday.api")
    generateAsInternal.set(true)
    codegenModels.set("responseBased")
}
