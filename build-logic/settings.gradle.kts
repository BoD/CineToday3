// See https://docs.gradle.org/current/userguide/platforms.html
enableFeaturePreview("VERSION_CATALOGS")

// Use the project's global version catalog in this module
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":convention")
