/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2022-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.android.cinetoday.api.apollo

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.LoggingInterceptor
import org.jraf.android.cinetoday.util.logging.logd

// GraphQL
private const val GRAPHQL_URL = "https://graph.allocine.fr/v1/mobile"

private const val HEADER_AUTHORIZATION_KEY = "Authorization"

// Unfortunately this will expire in June 2023 (see https://jwt.io/)
private const val HEADER_AUTHORIZATION_VALUE =
    "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE1NzE4NDM5NTcsInVzZXJuYW1lIjoiYW5vbnltb3VzIiwiYXBwbGljYXRpb25fbmFtZSI6Im1vYmlsZSIsInV1aWQiOiJmMDg3YTZiZi05YTdlLTQ3YTUtYjc5YS0zMDNiNWEwOWZkOWYiLCJzY29wZSI6bnVsbCwiZXhwIjoxNjg2NzAwNzk5fQ.oRS_jzmvfFAQ47wH0pU3eKKnlCy93FhblrBXxPZx2iwUUINibd70MBkI8C8wmZ-AeRhVCR8kavW8dLIqs5rUfA6piFwdYpt0lsAhTR417ABOxVrZ8dv0FX3qg1JLIzan-kSN4TwUZ3yeTjls0PB3OtSBKzoywGvFAu2jMYG1IZyBjxnkfi1nf1qGXbYsBfEaSjrj-LDV6Jjq_MPyMVvngNYKWzFNyzVAKIpAZ-UzzAQujAKwNQcg2j3Y3wfImydZEOW_wqkOKCyDOw9sWCWE2D-SObbFOSrjqKBywI-Q9GlfsUz-rW7ptea_HzLnjZ9mymXc6yq7KMzbgG4W9CZd8-qvHejCXVN9oM2RJ7Xrq5tDD345NoZ5plfCmhwSYA0DSZLw21n3SL3xl78fMITNQqpjlUWRPV8YqZA1o-UNgwMpOWIoojLWx-XBX33znnWlwSa174peZ1k60BQ3ZdCt9A7kyOukzvjNn3IOIVVgS04bBxl4holc5lzcEZSgjoP6dDIEJKib1v_AAxA34alVqWngeDYhd0wAO-crYW1HEd8ogtCoBjugwSy7526qrh68mSJxY66nr4Cle21z1wLC5lOsex0FbuwvOeFba0ycaI8NJPTUriOdvtHAjhDRSem4HjypGvKs5AzlZ3LAJACCHICNwo3NzYjcxfT4Wo1ur-M"

private const val HEADER_AC_AUTH_TOKEN_KEY = "AC-Auth-Token"

// This value was found by looking at the official app's network
private const val HEADER_AC_AUTH_TOKEN_VALUE =
    "fRCoWAfDyLs:APA91bF0V8MX1qMRDgG51FLWSZOYzec9vqTR74iWZdcrRUs-VeDF1LZoRmHcDhdNOr-7Z0WNnUi5TBTncvyRse4XbkpiEjvMgvVpBgAmeMMtW6wa8bKEcEUuXEw6xbW3ddhnrrpCYOrx"

internal fun createApolloClient(context: Context): ApolloClient {
//    val sqlNormalizedCacheFactory = SqlNormalizedCacheFactory(context, "apollo.db")

    return ApolloClient.Builder()
        .serverUrl(GRAPHQL_URL)
        .addHttpHeader(
            HEADER_AUTHORIZATION_KEY,
            HEADER_AUTHORIZATION_VALUE
        )
        .addHttpHeader(
            HEADER_AC_AUTH_TOKEN_KEY,
            HEADER_AC_AUTH_TOKEN_VALUE
        )
        // TODO Only enable in debug builds
        .addHttpInterceptor(LoggingInterceptor { logd("ApolloClient", it) })
        // TODO enable normalized cache, maybe
//        .normalizedCache(sqlNormalizedCacheFactory)
        .build()
}
