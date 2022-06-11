plugins {
    id("library-conventions")
    alias(libs.plugins.apollo)
}

dependencies {
    implementation(libs.apollo)
    implementation(libs.apollo.normalizedCache)
    implementation(libs.apollo.normalizedCache.sqlite)

    // For poster prefetching
    implementation(libs.coil.compose)
    implementation(libs.androidx.palette.ktx)
}

apollo {
    packageName.set("org.jraf.android.cinetoday.api")
    generateAsInternal.set(true)
    codegenModels.set("responseBased")

    mapScalar("DateTime", "java.util.Date", "org.jraf.android.cinetoday.api.apollo.DateTimeAdapter")
    mapScalar("DateInterval", "kotlin.Long", "org.jraf.android.cinetoday.api.apollo.DateIntervalAdapter")

    introspection {
        endpointUrl.set("https://graph.allocine.fr/v1/mobile")
        schemaFile.set(file("src/main/graphql/schema.graphqls"))
        headers.set(
            mapOf(
                "Authorization" to "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE1NzE4NDM5NTcsInVzZXJuYW1lIjoiYW5vbnltb3VzIiwiYXBwbGljYXRpb25fbmFtZSI6Im1vYmlsZSIsInV1aWQiOiJmMDg3YTZiZi05YTdlLTQ3YTUtYjc5YS0zMDNiNWEwOWZkOWYiLCJzY29wZSI6bnVsbCwiZXhwIjoxNjg2NzAwNzk5fQ.oRS_jzmvfFAQ47wH0pU3eKKnlCy93FhblrBXxPZx2iwUUINibd70MBkI8C8wmZ-AeRhVCR8kavW8dLIqs5rUfA6piFwdYpt0lsAhTR417ABOxVrZ8dv0FX3qg1JLIzan-kSN4TwUZ3yeTjls0PB3OtSBKzoywGvFAu2jMYG1IZyBjxnkfi1nf1qGXbYsBfEaSjrj-LDV6Jjq_MPyMVvngNYKWzFNyzVAKIpAZ-UzzAQujAKwNQcg2j3Y3wfImydZEOW_wqkOKCyDOw9sWCWE2D-SObbFOSrjqKBywI-Q9GlfsUz-rW7ptea_HzLnjZ9mymXc6yq7KMzbgG4W9CZd8-qvHejCXVN9oM2RJ7Xrq5tDD345NoZ5plfCmhwSYA0DSZLw21n3SL3xl78fMITNQqpjlUWRPV8YqZA1o-UNgwMpOWIoojLWx-XBX33znnWlwSa174peZ1k60BQ3ZdCt9A7kyOukzvjNn3IOIVVgS04bBxl4holc5lzcEZSgjoP6dDIEJKib1v_AAxA34alVqWngeDYhd0wAO-crYW1HEd8ogtCoBjugwSy7526qrh68mSJxY66nr4Cle21z1wLC5lOsex0FbuwvOeFba0ycaI8NJPTUriOdvtHAjhDRSem4HjypGvKs5AzlZ3LAJACCHICNwo3NzYjcxfT4Wo1ur-M",
                "AC-Auth-Token" to "fRCoWAfDyLs:APA91bF0V8MX1qMRDgG51FLWSZOYzec9vqTR74iWZdcrRUs-VeDF1LZoRmHcDhdNOr-7Z0WNnUi5TBTncvyRse4XbkpiEjvMgvVpBgAmeMMtW6wa8bKEcEUuXEw6xbW3ddhnrrpCYOrx",
            )
        )
    }
}

// `./gradlew :api:downloadServiceApolloSchemaFromIntrospection` to download the schema
